package com.ishwal.beverageinventory.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.github.mikephil.charting.animation.Easing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ishwal.beverageinventory.BaseActivity
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.ActivityDashboardBinding
import com.ishwal.beverageinventory.ui.inventory.InventoryActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.components.Legend
import com.ishwal.beverageinventory.ui.addinventory.AddInventoryItemActivity
import com.ishwal.beverageinventory.ui.setting.SettingActivity
import com.ishwal.beverageinventory.utils.chart.InventoryChartValueFormatter
import com.ishwal.beverageinventory.utils.scanner.ScanResultModel
import com.ishwal.beverageinventory.utils.scanner.ScannerActivity
import timber.log.Timber


class DashboardActivity : BaseActivity() {

    companion object{
        val TAG: String = DashboardActivity::class.java.simpleName
        const val SCANNER_REQUEST_CODE = 10001
    }

    private val auth: FirebaseAuth by inject()

    private val dashboardViewModel : DashboardViewModel by viewModel()

    private val databaseReferenceForItem: DatabaseReference by inject(qualifier = named("ITEM"))

    private lateinit var activityDashboardBinding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        activityDashboardBinding.dashboardViewModel = dashboardViewModel

        addManuallyCard.setOnClickListener{
            startActivity(Intent(this,
                AddInventoryItemActivity::class.java))
        }

        viewInventoryCard.setOnClickListener{
            startActivity(Intent(this,
                InventoryActivity::class.java))
        }

        addByScanCard.setOnClickListener {
            val i = Intent(this, ScannerActivity::class.java)
            startActivityForResult(i, SCANNER_REQUEST_CODE)
        }

        settingCard.setOnClickListener {
            startActivity(Intent(this,
                SettingActivity::class.java))
        }


        inventoryChart.setNoDataText("There is no item in inventory")

        dashboardViewModel.handlePieChartData.observe(this, Observer {
            //Prepare pie chart
            if(it.isEmpty()){
                inventoryChart.clear()
                inventoryChart.setNoDataText("There is no item in inventory")
            }else {
                val entries: MutableList<PieEntry> = mutableListOf()

                it.keys.forEach { productType ->
                    var quantity = 0
                    it[productType]?.forEach { item ->
                        quantity += item.productQuantity ?: 0
                    }
                    entries.add(PieEntry(quantity.toFloat(), productType))
                }

                val dataSet = PieDataSet(entries, "My Inventory")

                val colors: MutableList<Int> = mutableListOf()


                for (c in ColorTemplate.MATERIAL_COLORS)
                    colors.add(c)

                for (c in ColorTemplate.JOYFUL_COLORS)
                    colors.add(c)

                for (c in ColorTemplate.COLORFUL_COLORS)
                    colors.add(c)

                for (c in ColorTemplate.LIBERTY_COLORS)
                    colors.add(c)

                for (c in ColorTemplate.PASTEL_COLORS)
                    colors.add(c)

                colors.add(ColorTemplate.getHoloBlue())


                dataSet.colors = colors

                val data = PieData(dataSet)
                data.setValueFormatter(InventoryChartValueFormatter())
                data.setValueTextSize(10f)
                data.setValueTextColor(Color.WHITE)
                inventoryChart.data = data

                inventoryChart.centerText = "${dashboardViewModel.itemCount.get()} Items"
                inventoryChart.description.isEnabled = false
                inventoryChart.animateY(1400, Easing.EaseInOutQuad)

                val l = inventoryChart.legend
                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                l.orientation = Legend.LegendOrientation.VERTICAL
                l.setDrawInside(false)

                // undo all highlights
                inventoryChart.highlightValues(null)

                inventoryChart.invalidate()
            }

        })


        initializeView()
    }

    private fun initializeView() {
        val currentUser = auth.currentUser
        if(currentUser != null) {

            dashboardViewModel.initializeUserDetail(currentUser)

            val query =  databaseReferenceForItem.child(currentUser.uid)
            query.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    hideProgressDialog()
                    Toast.makeText(this@DashboardActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    hideProgressDialog()
                    if (dataSnapshot.exists()) {
                        dashboardViewModel.initializeItem(dataSnapshot)
                    } else {
                        //to reload chart
                        dashboardViewModel.initializeItem(null)
                        Toast.makeText(
                            this@DashboardActivity,
                            "No item Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val scanResult = data.getSerializableExtra("barcodeData") as ScanResultModel
                    val addInventoryItemActivity = Intent(this@DashboardActivity, AddInventoryItemActivity::class.java)
                    addInventoryItemActivity.putExtra("CODE", scanResult.barcodeData)
                    startActivity(addInventoryItemActivity)
                }
            }
        } else {
            //Scan cancelled by user
            Timber.d("Scan cancelled by user")
        }
    }
}
