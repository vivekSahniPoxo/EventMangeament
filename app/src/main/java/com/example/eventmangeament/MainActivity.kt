package com.example.eventmangeament

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eventmangeament.database.EventDao
import com.example.eventmangeament.database.EventDatabase
import com.example.eventmangeament.databinding.ActivityMainBinding
import com.example.eventmangeament.retrofit.RetrofitApi
import com.example.eventmangeament.retrofit.RetrofitClient
import com.example.eventmangeament.userinfo.*
import com.example.eventmangeament.utils.Cons
import com.google.android.material.snackbar.Snackbar
import com.speedata.libuhf.IUHFService
import com.speedata.libuhf.UHFManager
import com.speedata.libuhf.utils.ErrorStatus
import com.speedata.libuhf.utils.StringUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var dialog: Dialog

    lateinit var lunchList: ArrayList<String>
    lateinit var dinnerList: ArrayList<String>
    lateinit var dinnerAndLuchList: ArrayList<String>
    var selectedRdButton = ""
    lateinit var iuhfService: IUHFService
    var RfidNo = ""
    var foundEpcForPush = 0
    lateinit var  handler: Handler

    lateinit var myEventDataBase:EventDatabase
    lateinit var eventDao:EventDao
    lateinit var userEnterDetailList:ArrayList<UserEntryDetails>
    var foundEpcFrom = ""
    var rfidFromAscannedTebale = ""
    var selectedValue = ""
    lateinit var progressDialog: ProgressDialog
    lateinit var allScannedDetails:ArrayList<AllScannedDetails>
    lateinit var pushRequestBody: ArrayList<RfidRequestBody>

    var userTypeList = arrayListOf<String>()

    var select = 0

    var pageValue = 1

    var responseBody = ""

    lateinit var rfidList:ArrayList<String>



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        lunchList = arrayListOf()
        dinnerList = arrayListOf()
        dinnerAndLuchList = arrayListOf()
        userEnterDetailList = arrayListOf()
        allScannedDetails = arrayListOf()
        pushRequestBody = arrayListOf()

        rfidList = arrayListOf()

        rfidList.add("E280699500004007FAD4D9DA")
        rfidList.add("E280699500005007FAD4DDDA")
        rfidList.add("E280699500005007FAD4EDDA")
        rfidList.add("E280699500004007FAD4F1DA")
        rfidList.add("E280699500004007FAD4E9DA")
        rfidList.add("E280699500004007FAD4E5DA")
        rfidList.add("E280699500004007FAD4D5DA")
        rfidList.add("E280699500005007FAD4D1DA")
        rfidList.add("E280699500005007FAD4E1DA")

        rfidList.add("E280699500005007FAD4C5DA")
        rfidList.add("E280699500005007FAD4C9DA")
        rfidList.add("E280699500005007FAD4C5DA")
       // rfidList.add("E280699500005007FAD4C5DA")








        progressDialog = ProgressDialog(this@MainActivity)

        handler = Handler()
        userTypeList.add("Choose Access Point")
        //userTypeList.add("FullAccess")
        userTypeList.add("Food")
       // userTypeList.add("Dinner")
        userTypeList.add("Power Time Zone")
        userTypeList.add("Event")
        selectUserType()

        binding.cardChaneUrl.setOnClickListener {
            val intent = Intent(this@MainActivity,SettingsActivity::class.java)
            startActivity(intent)

        }



         myEventDataBase = EventDatabase.getDatabase(this@MainActivity)
         eventDao = myEventDataBase.eventDao()

//        GlobalScope.launch {
//            eventDao.syncEntryDetails(SyncEntryDetails(0, "E280117000000214249CA4C9", "Lunch"))
//        }

        lifecycleScope.launch {
            val rowCount = eventDao.getRowCount()
            binding.tvStatus.text = "Registered Guests $rowCount"
        }


        binding.pushCard.setOnClickListener {
            val allData = eventDao.getAllData()
            if (allData.isNotEmpty()){
                popAlert()
            } else {
             Snackbar.make(binding.root,"No Records",Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.rdCard.setOnClickListener {
//            val allData = eventDao.getAllData()
//            if (allData.isNotEmpty()){
//                popAlert()
//            } else {
               // progressDialog = ProgressDialog(this@MainActivity)
               sync()
//                progressDialog.setMessage("Please wait...")
//                progressDialog.setCancelable(false)
//                progressDialog.show()
               // fetchDataFromApi()
//                page = 1
//                while (responseBody!=null) {
                   // getAllData(pageValue)
//                    break
//                }
            //}

            lifecycleScope.launch {
                val rowCount = eventDao.getRowCount()
                binding.tvStatus.text = "Registered Guests $rowCount"
            }
        }


        try {
            iuhfService = UHFManager.getUHFService(this)
            iuhfService.openDev()
            iuhfService.antennaPower = 30
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        }



//        binding.groupradio.setOnCheckedChangeListener { group, checkedId ->
//            when (checkedId) {
//                R.id.radio_luch -> {
//                    selectedRdButton = "lunch"
//
//                }
//                R.id.radio_dinner -> {
//                    selectedRdButton = "dinner"
//                }
//
//            }
//        }


//        val animation = AnimationUtils.loadAnimation(this, R.anim.animated_text)
//
//        // Add an AnimationListener to restart the animation when it finishes
//        animation.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation?) {}
//            override fun onAnimationRepeat(animation: Animation?) {}
//            override fun onAnimationEnd(animation: Animation?) {
//                binding.animatedText.startAnimation(animation)
//            }
//        })
//
//        // Start the animation
//        binding.animatedText.startAnimation(animation)


    }

    @SuppressLint("WrongViewCast", "SuspiciousIndentation")
    private fun alertDialog(getName:String,company:String,image:String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_for_approve)
        dialog.setCancelable(true)
        dialog.show()
        val name = dialog.findViewById<TextView>(R.id.tv_name)
        val companyName = dialog.findViewById<TextView>(R.id.tv_company_name)
        val profileImg =dialog.findViewById<CircleImageView>(R.id.profile_image)
        try {

            name.text = getName
            companyName.text = company
         //val image2 = "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"

            if (image.startsWith("data:image/png;base64,")) {
                val guest = image.removePrefix("data:image/png;base64,")
                val imageBytes = android.util.Base64.decode(guest, 0)
                val img = convertCompressedByteArrayToBitmap(imageBytes)
                profileImg.setImageBitmap(img)
            } else{
                val imageBytes = android.util.Base64.decode(image, 0)
                val img = convertCompressedByteArrayToBitmap(imageBytes)
                profileImg.setImageBitmap(img)
            }
        } catch (e:Exception){
            Log.d("Exception",e.toString())
        }

        val submit = dialog.findViewById<Button>(R.id.btn_approved)
        submit.setOnClickListener {
            dialog.dismiss()

        }


    }

    fun convertCompressedByteArrayToBitmap(src: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(src, 0, src.size)
    }


    private fun selectUserType(){

        val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(this, androidx.appcompat.R.layout.select_dialog_item_material, userTypeList ) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView =
                    super.getDropDownView(position, convertView, parent) as TextView

                if (position == binding.spType.selectedItemPosition && position != 0) {
                    view.setTextColor(Color.parseColor("#000000"))
                }
                if (position == 0) {
                    view.setTextColor(Color.parseColor("#999999"))
                }


                return view
            }

            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        binding.spType.adapter = adapter

        binding.spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                parent.getItemAtPosition(position).toString()
                if (parent.getItemAtPosition(position).toString() != "Choose Access Point") {
                        selectedValue = parent.getItemAtPosition(position).toString()

                    select = parent.selectedItemPosition
                        Log.d("sle", selectedValue)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    @SuppressLint("WrongViewCast")
    private fun alreadyExits() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_user_already)
        dialog.setCancelable(true)

        // Find the "Submit" button within the dialog's view
        val submit = dialog.findViewById<Button>(R.id.ok)
//        val foundfromEpc = dialog.findViewById<TextView>(R.id.foundFromEpc)
//        foundfromEpc.text = foundEpcFrom

        submit.setOnClickListener {
            // Dismiss the dialog when the "Submit" button is clicked
            dialog.dismiss()
            GlobalScope.launch(Dispatchers.IO) {
                eventDao.eventEntryDetails(UserEntryDetails(0, RfidNo, foundEpcFrom))
                addEpcInTextFile(RfidNo,this@MainActivity)
            }
        }

        dialog.show()
    }



    private fun notAllowed() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_not_allowed)
        dialog.setCancelable(true)

        // Find the "Submit" button within the dialog's view
        val submit = dialog.findViewById<Button>(R.id.btn_submit)
//        val foundfromEpc = dialog.findViewById<TextView>(R.id.foundFromEpc)
//        foundfromEpc.text = foundEpcFrom

        submit.setOnClickListener {
            // Dismiss the dialog when the "Submit" button is clicked
            dialog.dismiss()
            GlobalScope.launch(Dispatchers.IO) {
                eventDao.eventEntryDetails(UserEntryDetails(0, RfidNo, foundEpcFrom))

            }
        }

        dialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == 105 || keyCode == KeyEvent.KEYCODE_F1) {

            iuhfService.setOnReadListener { var1 ->
                val stringBuilder = StringBuilder()
                val epcData = var1.epcData
                val hexString = StringUtils.byteToHexString(epcData, var1.epcLen)
                if (!TextUtils.isEmpty(hexString)) {
                    stringBuilder.append("EPC：").append(hexString).append("\n")
                }
                if (var1.status == 0) {
                    val readData = var1.readData
                    val readHexString = StringUtils.byteToHexString(readData, var1.dataLen)
                    stringBuilder.append("ReadData:").append(readHexString).append("\n")
                    Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
                    Log.d("rffifif",readHexString)
                    RfidNo = readHexString
                    val currenttime = SimpleDateFormat("HH:mm:ss a", Locale.getDefault()).format(java.util.Date())
                   // val currenttime = LocalDateTime.now()
                    Log.d("currrent",currenttime.toString())


                   // 2023-09-27T18:13:55.263
                   // try {
                        val getDataFromSyncTable = eventDao.getDataFromSyncTable(RfidNo)
                        val allDataFormScannedTable = eventDao.getAllScannedDetails(RfidNo)
                        val selectedPosition = binding.spType.selectedItemPosition
                       if (rfidList.contains(readHexString)){
                        alertDialog("Team","xyz","image")

                       } else if (getDataFromSyncTable==null) {
                           notAllowed()
                       } else if(selectedPosition==0){
                           Snackbar.make(binding.root,"Please Select Access Point",Snackbar.LENGTH_SHORT).show()
                        } else if (selectedPosition == 1 ){
                                if(getDataFromSyncTable.rfidno == readHexString ) {
                                    if (currenttime.toString() >= "08:00:00 AM" && currenttime.toString() <= "16:00:00 PM") {
                                        if (getDataFromSyncTable.lunchAccess == 1) {
                                            if (allDataFormScannedTable != null && allDataFormScannedTable.lunchAccess == 1) {
                                                alreadyExits()
                                            } else if (allDataFormScannedTable == null) {
                                                GlobalScope.launch {
                                                eventDao.allScannedDetails(
                                                    AllScannedDetails(
                                                        0,
                                                        0,
                                                        0,
                                                        0,
                                                        1,0,
                                                        readHexString
                                                    )
                                                )}
                                                alertDialog(
                                                    getDataFromSyncTable.name,
                                                    getDataFromSyncTable.companyName,
                                                    getDataFromSyncTable.image
                                                )
                                            } else{
                                                GlobalScope.launch {
                                                    allDataFormScannedTable.lunchAccess = 1
                                                    eventDao.updateVehicleImage(
                                                        allDataFormScannedTable
                                                    )}
                                                alertDialog(
                                                    getDataFromSyncTable.name,
                                                    getDataFromSyncTable.companyName,
                                                    getDataFromSyncTable.image
                                                )
                                        }
                                            } else{
                                                notAllowed()
                                        }

                                    } else if (currenttime.toString() >= "16:00:01 PM" && currenttime.toString() <= "23:00:00 PM") {
                                        if (getDataFromSyncTable.dinnerAccess == 1) {
                                            if (allDataFormScannedTable!=null && allDataFormScannedTable.dinnerAccess == 1) {
                                                alreadyExits()
                                            } else if (allDataFormScannedTable==null) {
                                                GlobalScope.launch {
                                                      eventDao.allScannedDetails(AllScannedDetails(0, 1, 0, 0, 0,0,readHexString)) }
                                                    alertDialog(
                                                        getDataFromSyncTable.name,
                                                        getDataFromSyncTable.companyName,
                                                        getDataFromSyncTable.image
                                                    )
                                                } else {
                                                GlobalScope.launch {
                                                    allDataFormScannedTable.dinnerAccess = 1
                                                    eventDao.updateVehicleImage(allDataFormScannedTable

                                                    )}

                                                alertDialog(
                                                    getDataFromSyncTable.name,
                                                    getDataFromSyncTable.companyName,
                                                    getDataFromSyncTable.image
                                                )
                                            }
                                            } else{
                                                notAllowed()
                                        }
                                        } else{
                                            notAllowed()
                                    }

                                    } else {
                                        notAllowed()
                                    }
                                //}
                            } else if(selectedPosition == 2){
                                if(getDataFromSyncTable.rfidno== readHexString && getDataFromSyncTable.powerMeetingAccess== 1) {
                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
                                    if (allDataFormScannedTable!=null&&allDataFormScannedTable.powerMeetingAccess==1){
                                        alreadyExits()
                                    } else if(allDataFormScannedTable==null){
                                       // GlobalScope.launch {
//                                            allDataFormScannedTable.dinnerAccess = 1
//                                             eventDao.updateVehicleImage(allDataFormScannedTable)
                                            GlobalScope.launch {
                                                eventDao.allScannedDetails(AllScannedDetails(0, 0, 0, 0, 0,1,readHexString)) }

                                       // }
                                        alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.companyName,getDataFromSyncTable.image)
                                    } else {
                                        GlobalScope.launch {
                                            allDataFormScannedTable.powerMeetingAccess = 1
                                             eventDao.updateVehicleImage(allDataFormScannedTable)}
                                        alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.companyName,getDataFromSyncTable.image)
//                                    }
//                                    else if (allDataFormScannedTable.powerMeetingAccess==1){
//                                        allDataFormScannedTable.powerMeetingAccess = 2
//                                        eventDao.updateVehicleImage(allDataFormScannedTable)
//                                        alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.companyName,getDataFromSyncTable.image)
//
                                   }

                                } else {
                                    notAllowed()
                                }

                            } else if (selectedPosition==3){
                                if(getDataFromSyncTable.rfidno== readHexString && getDataFromSyncTable.eventAccess== 1) {
                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
                                    if (allDataFormScannedTable!=null && allDataFormScannedTable.eventAccess==1) {
                                        alertDialog(
                                            getDataFromSyncTable.name,
                                            getDataFromSyncTable.companyName,
                                            getDataFromSyncTable.image
                                        )
                                        GlobalScope.launch {
                                            allDataFormScannedTable.eventAccess = 1
                                            eventDao.updateVehicleImage(allDataFormScannedTable

                                            )}
                                    } else if (allDataFormScannedTable==null){
                                        alertDialog(
                                            getDataFromSyncTable.name,
                                            getDataFromSyncTable.companyName,
                                            getDataFromSyncTable.image
                                        )
                                        GlobalScope.launch {
                                            eventDao.allScannedDetails(
                                                AllScannedDetails(
                                                    0,
                                                    0,
                                                    1,
                                                    0,
                                                    0,
                                                    0,
                                                    readHexString
                                                )
                                            )
                                    }
                                    } else{
                                        alertDialog(
                                            getDataFromSyncTable.name,
                                            getDataFromSyncTable.companyName,
                                            getDataFromSyncTable.image
                                        )
                                        GlobalScope.launch {
                                            allDataFormScannedTable.eventAccess = 1
                                            eventDao.updateVehicleImage(allDataFormScannedTable

                                            )}
                                    }
                                } else {
                                    notAllowed()
                                }
                            }






//                          } else if (selectedPosition == 1) {
//                            if (getDataFromSyncTable.rfidno == readHexString) {
//                                    alertDialog(getDataFromSyncTable.name, getDataFromSyncTable.companyName, getDataFromSyncTable.image)
//                                    GlobalScope.launch {
//                                        eventDao.allScannedDetails(
//                                            AllScannedDetails(0, 0, 1, 0, 0, readHexString)) }
//
//                            }
//                        } else if (selectedPosition == 2) {
//                            if (getDataFromSyncTable.rfidno == readHexString && getDataFromSyncTable.lunchAccess == 1) {
//                                if (allDataFormScannedTable.lunchAccess == 1) {
//                                    alreadyExits()
//                                } else if (allDataFormScannedTable == null) {
//                                    alertDialog(
//                                        getDataFromSyncTable.name,
//                                        getDataFromSyncTable.companyName,
//                                        getDataFromSyncTable.image
//                                    )
//                                    GlobalScope.launch {
//                                        eventDao.allScannedDetails(
//                                            AllScannedDetails(0, 0, 0, 1, 0, readHexString)
//                                        )
//                                    }
//
//                                } else if (allDataFormScannedTable != null) {
//                                    GlobalScope.launch {
//                                        allDataFormScannedTable.lunchAccess = 1
//                                        eventDao.updateVehicleImage(allDataFormScannedTable)
//                                    }
//                                } else {
//                                    notAllowed()
//                                }
//                            }
//                        } else if (selectedPosition == 3) {
//                            if (getDataFromSyncTable.rfidno == readHexString && getDataFromSyncTable.dinnerAccess == 1) {
//                                if (allDataFormScannedTable.dinnerAccess == 1) {
//                                    alreadyExits()
//                                } else if (allDataFormScannedTable == null) {
//                                    alertDialog(
//                                        getDataFromSyncTable.name,
//                                        getDataFromSyncTable.companyName,
//                                        getDataFromSyncTable.image
//                                    )
//                                    GlobalScope.launch {
//                                        eventDao.allScannedDetails(
//                                            AllScannedDetails(1, 0, 0, 0, 0, readHexString)
//                                        )
//                                    }
//
//
//                                } else if (allDataFormScannedTable != null) {
//                                    GlobalScope.launch {
//                                        allDataFormScannedTable.dinnerAccess = 1
//                                        eventDao.updateVehicleImage(allDataFormScannedTable)
//                                    }
//                                } else {
//                                    notAllowed()
//                                }
//                            }
//                        } else if (selectedPosition == 4) {
//                            if (getDataFromSyncTable.rfidno == readHexString && getDataFromSyncTable.powerMeetingAccess == 1) {
//                                if (allDataFormScannedTable.powerMeetingAccess == 1) {
//                                    alreadyExits()
//                                } else if (allDataFormScannedTable == null) {
//                                    alertDialog(
//                                        getDataFromSyncTable.name,
//                                        getDataFromSyncTable.companyName,
//                                        getDataFromSyncTable.image
//                                    )
//                                    GlobalScope.launch {
//                                        eventDao.allScannedDetails(
//                                            AllScannedDetails(0, 0, 0, 0, 1, readHexString)
//                                        )
//                                    }
//
//                                } else if (allDataFormScannedTable != null) {
//                                    GlobalScope.launch {
//                                        allDataFormScannedTable.powerMeetingAccess = 1
//                                        eventDao.updateVehicleImage(allDataFormScannedTable)
//                                    }
//                                } else {
//                                    notAllowed()
//                                }
//                            }
//                        } else if (selectedPosition == 5) {
//                            if (getDataFromSyncTable.rfidno == readHexString && getDataFromSyncTable.eventAccess == 1) {
//                                alertDialog(
//                                    getDataFromSyncTable.name,
//                                    getDataFromSyncTable.companyName,
//                                    getDataFromSyncTable.image
//                                )
//                                GlobalScope.launch {
//                                    eventDao.allScannedDetails(AllScannedDetails(0, 1, 0, 0, 0, readHexString))
//                                }
//                            } else {
//                                alertDialog(
//                                    getDataFromSyncTable.name,
//                                    getDataFromSyncTable.companyName,
//                                    getDataFromSyncTable.image
//                                )
//                            }
//                        }
//                    } catch (e:Exception){
//                        Log.d("exce",e.toString())
//                    }







//                    try {
//                        val getDataFromSyncTable = eventDao.getDataFromSyncTable(RfidNo)
//                        val allDataFormScannedTable = eventDao.getAllScannedDetails(RfidNo)
//                        val selectedPosition = binding.spType.selectedItemPosition
//
//                        if (selectedPosition==0) {
//                            Snackbar.make(binding.root, "Please Choose Access Point", Snackbar.LENGTH_SHORT).show()
//                        } else
//                            if (getDataFromSyncTable==null) {
//                               notAllowed()
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                        } else if(getDataFromSyncTable !=null && getDataFromSyncTable.rfidNo !=readHexString) {
//                            notAllowed()
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                        } else if(allDataFormScannedTable==null) {
//                                if (getDataFromSyncTable != null && getDataFromSyncTable.rfidNo == readHexString && getDataFromSyncTable.isfood == "true" && select==1) {
//                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                    alertDialog(getDataFromSyncTable.name, getDataFromSyncTable.company, getDataFromSyncTable.image)
//                                    GlobalScope.launch {
//                                        eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 1, 0, 0))
//                                    }
//                                }else if (getDataFromSyncTable.rfidNo == readHexString && getDataFromSyncTable.ispowertimezone == "true" && select==2) {
//                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                    GlobalScope.launch { eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 0, 1, 0))
//                                    }
//                                    alertDialog(getDataFromSyncTable.name, getDataFromSyncTable.company, getDataFromSyncTable.image)
//                                } else if (getDataFromSyncTable != null && getDataFromSyncTable.rfidNo == readHexString && getDataFromSyncTable.isexhibition == "true" && select==3) {
//                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                    alertDialog(
//                                        getDataFromSyncTable.name,
//                                        getDataFromSyncTable.company,
//                                        getDataFromSyncTable.image
//                                    )
//                                    GlobalScope.launch {
//                                        eventDao.allScannedDetails(
//                                            AllScannedDetails(
//                                                0,
//                                                readHexString,
//                                                0,
//                                                0,
//                                                1
//                                            )
//                                        )
//                                    }
//                                }
////
//                            }
//
//                            if (select ==1 ){
//                                if(getDataFromSyncTable.rfidNo== readHexString && getDataFromSyncTable.isfood== "true") {
//                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                     if (allDataFormScannedTable.isFood==1){
//                                         alreadyExits()
//                                     } else{
//                                         GlobalScope.launch {
//                                             allDataFormScannedTable.isFood = 1
//                                             eventDao.updateVehicleImage(allDataFormScannedTable)
//                                         }
//                                             //eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 1, 0, 0)) }
//                                         alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.company,getDataFromSyncTable.image)
//                                     }
//
//
//                                } else {
//                                    notAllowed()
//                                }
//                            } else if(select ==2){
//                                if(getDataFromSyncTable.rfidNo== readHexString && getDataFromSyncTable.ispowertimezone== "true") {
//                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                    if (allDataFormScannedTable.IsPTZ==1){
//                                        alreadyExits()
//                                    } else{
//                                        GlobalScope.launch {
//                                            allDataFormScannedTable.IsPTZ = 1
//                                             eventDao.updateVehicleImage(allDataFormScannedTable)
//                                            //eventDao.allScannedDetails(AllScannedDetails(0, readHexString,0 , 1, 0))
//                                        }
//                                        alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.company,getDataFromSyncTable.image)
//                                    }
//                                } else {
//                                    notAllowed()
//                                }
//
//                            } else if (select==3){
//                                if(getDataFromSyncTable.rfidNo== readHexString && getDataFromSyncTable.isexhibition== "true") {
//                                    Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                    if (allDataFormScannedTable.isexhibition==1){
//                                        alreadyExits()
//                                    } else{
//                                        GlobalScope.launch {
//                                            allDataFormScannedTable.isexhibition = 1
//                                            eventDao.updateVehicleImage(allDataFormScannedTable)
//                                        }
//                                            //eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 0, 0, 1)) }
//                                        alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.company,getDataFromSyncTable.image)
//                                    }
//
//
//                                } else {
//                                    notAllowed()
//                                }
//                            }
//
//
//
//
//
//                            else if(allDataFormScannedTable!=null && allDataFormScannedTable.RfidNo== readHexString && allDataFormScannedTable.IsPTZ== 1 && select==2){
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                                alreadyExits()
//                            } else if(allDataFormScannedTable!=null && allDataFormScannedTable.RfidNo== readHexString && allDataFormScannedTable.isexhibition== 1 && select == 3){
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                                alreadyExits()
//                            } else if(allDataFormScannedTable!=null && allDataFormScannedTable.RfidNo== readHexString && allDataFormScannedTable.isFood== 1 && allDataFormScannedTable.IsPTZ==1 && allDataFormScannedTable.isexhibition==1) {
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                                alreadyExits()
//                            }
//                            else if (getDataFromSyncTable != null && getDataFromSyncTable.rfidNo == readHexString && getDataFromSyncTable.ispowertimezone == "false") {
//                                notAllowed()
//
//                        } else if (getDataFromSyncTable!=null && getDataFromSyncTable.rfidNo==readHexString && getDataFromSyncTable.isexhibition == "true" && select==3) {
//                                Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                alertDialog(getDataFromSyncTable.name, getDataFromSyncTable.company, getDataFromSyncTable.image)
//                                GlobalScope.launch { eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 0, 0, 1))
//                                }
//                            } else if (getDataFromSyncTable != null && getDataFromSyncTable.rfidNo == readHexString && getDataFromSyncTable.isexhibition == "false") {
//                                notAllowed()
//
//                        } else if(getDataFromSyncTable!=null && getDataFromSyncTable.rfidNo==readHexString && getDataFromSyncTable.isfood == "true" && getDataFromSyncTable.ispowertimezone == "true") {
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                            alertDialog(getDataFromSyncTable.name, getDataFromSyncTable.company, getDataFromSyncTable.image)
//                            GlobalScope.launch {
//                                eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 1, 1, 0))
//                            }
//                        } else if (getDataFromSyncTable!=null && getDataFromSyncTable.rfidNo==readHexString && getDataFromSyncTable.isfood == "true" && getDataFromSyncTable.isexhibition == "true") {
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                            alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.company, getDataFromSyncTable.image)
//                            GlobalScope.launch { eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 1, 0, 1))
//                            }
//                        } else if (getDataFromSyncTable!=null && getDataFromSyncTable.rfidNo==readHexString && getDataFromSyncTable.ispowertimezone == "true" && getDataFromSyncTable.isexhibition == "true") {
//                                Toast.makeText(this,readHexString,Toast.LENGTH_SHORT).show()
//                            alertDialog(getDataFromSyncTable.name, getDataFromSyncTable.company, getDataFromSyncTable.image)
//                            GlobalScope.launch { eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 0, 1, 1))
//                            }
//
//                        } else if(getDataFromSyncTable!=null && getDataFromSyncTable.rfidNo==readHexString && getDataFromSyncTable.isfood == "true" && getDataFromSyncTable.ispowertimezone == "true" && getDataFromSyncTable.isexhibition=="true") {
//                                Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                                alertDialog(
//                                    getDataFromSyncTable.name,
//                                    getDataFromSyncTable.company,
//                                    getDataFromSyncTable.image
//                                )
//                                GlobalScope.launch {
//                                    eventDao.allScannedDetails(
//                                        AllScannedDetails(
//                                            0,
//                                            readHexString,
//                                            1,
//                                            1,
//                                            1
//                                        )
//                                    )
//                                }
//                            }
//
//
//
//
//                    } catch (e:Exception){
//                        Log.d("exception",e.toString())
//                    }



                } else {
                    stringBuilder.append(this.resources.getString(R.string.read_fail)).append(":").append(
                        ErrorStatus.getErrorStatus(this,var1.status)).append("\n")
                }
                handler.sendMessage(handler.obtainMessage(1, stringBuilder))

            }
            val readArea = iuhfService.readArea(1, 2, 6, "00000000")
            if (readArea != 0) {
                val err: String = this.resources.getString(R.string.read_fail) + ":" + ErrorStatus.getErrorStatus(this ,readArea) + "\n"
                handler.sendMessage(handler.obtainMessage(1, err))

            }

            return true
        }
        else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                // startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getAllData(page: Int){
        RetrofitClient.getResponseFromApi().getAllData(pageValue,25).enqueue(object :
            Callback<GetDataFromApi> {
            @SuppressLint("SuspiciousIndentation")
            override  fun onResponse(call: Call<GetDataFromApi>, response: Response<GetDataFromApi>) {
                dialog.dismiss()
                if (response.code() == 200) {
                    progressDialog.dismiss()
                    responseBody = response.body().toString()
                        getApiCall()

                    Log.d("responsBody2", responseBody)

                    try{
                    val getData = response.body()
                    if (getData != null) {
                        for (item in getData) {

                           // val data = TempData(item.id,item.category)
                            GlobalScope.launch(Dispatchers.IO) {
                                val rowCount = eventDao.getRowCount()
                                lifecycleScope.launch {
                                    binding.tvStatus.text = "Registered Guests $rowCount"
                                }
                            }
//                            GlobalScope.launch {
//                                eventDao.TempData(data)
//                            }

                            val addSyncData = SyncEntryDetails(
                                item.id,
                                item.category,
                                item.chapterName,
                                item.companyName,
                                item.dinnerAccess,
                                item.emailAddress,
                                item.eventAccess,
                                item.fullAccess,
                                item.image,
                                item.lunchAccess,
                                item.name,
                                item.phoneNumber,
                                item.powerMeetingAccess,
                                item.rfidno
                            )

                            GlobalScope.launch(Dispatchers.IO) {
                                eventDao.syncEntryDetails(addSyncData)
                            }


                        }





                    }
                    } catch (e:Exception){
                        Log.d("exception",e.toString())
                    }



                } else if (response.code() == 400) {
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_SHORT).show()
                } else if (response.code() == 500) {
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_SHORT).show()
                } else if (response.code() == 404) {
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, response.message(), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<GetDataFromApi>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        })
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun getApiCall(){
        if (responseBody!=null) {
            //progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.show()
           // Snackbar.make(binding.root,"Adding data",Toast.LENGTH_SHORT).show()
            pageValue++
            getAllData(pageValue)

            GlobalScope.launch(Dispatchers.IO) {
                val rowCount = eventDao.getRowCount()
                lifecycleScope.launch {
                    binding.tvStatus.text = "Registered Guests $rowCount"
                }
            }
            while (responseBody == null) {
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "AllDataAdded", Toast.LENGTH_SHORT).show()
                break
                progressDialog.dismiss()
//            pageValue++
                Log.d("pageValue", pageValue.toString())
            }
        } else{
            progressDialog.dismiss()
            Toast.makeText(this@MainActivity, "AllDataAdded", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.M)
    fun popAlert(){
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.apply {
            setTitle("Alert")
            setMessage("Are sure you want to push the data on server.")
            setPositiveButton("OK") { dialog, _ ->
                val allData = eventDao.getAllData()
                allData.forEach { pushRequestBody.add(RfidRequestBody(it.dinnerAccess,it.eventAccess,it.fullAccess,it.lunchAccess,it.powerMeetingAccess,it.rfidNumber))
                }
                    pustDataOnServer(pushRequestBody)

                    // Code to execute when OK button is clicked
                // For example, you can perform an action or dismiss the dialog
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                // Code to execute when Cancel button is clicked
                // For example, you can dismiss the dialog
                dialog.dismiss()
            }
        }

        // Create and show the dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun sync(){
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.apply {
                setTitle("Alert")
                setMessage("Are sure you want to sync the data.")
                setPositiveButton("OK") { dialog, _ ->
                    progressDialog.setMessage("Please wait...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    getAllData(pageValue)
                    // Code to execute when OK button is clicked
                    // For example, you can perform an action or dismiss the dialog
                    dialog.dismiss()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    // Code to execute when Cancel button is clicked
                    // For example, you can dismiss the dialog
                    dialog.dismiss()
                }
            }

            // Create and show the dialog
            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }





    @RequiresApi(Build.VERSION_CODES.M)
    private fun pustDataOnServer(pushRequestBody: ArrayList<RfidRequestBody>){
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        RetrofitClient.getResponseFromApi().pushDataOnServer(pushRequestBody).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                dialog.dismiss()
                if (response.code()==200) {
                    progressDialog.dismiss()
                    //eventDao.deleteSync()
                   // eventDao.deleteAllScanned()
                    //getAllData(1)
                    //myEventDataBase.close()
                    Toast.makeText(this@MainActivity,response.body(),Toast.LENGTH_SHORT).show()

                } else if (response.code()==400){
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity,response.message(),Toast.LENGTH_SHORT).show()
                } else if (response.code()==500){
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity,response.message(),Toast.LENGTH_SHORT).show()
                } else if (response.code()==404){
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity,response.message(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.localizedMessage,Toast.LENGTH_SHORT).show()
            }

        })
    }




    fun addEpcInTextFile(EpcNo: String, context: Context) {
        val fileName = "data.txt"

        // Sample data to be written to the file
        val newData = EpcNo

        try {
            // Open the file in append mode within the app's files directory
            val file = File(context.filesDir, fileName)
            val fileWriter = FileWriter(file, true) // Set the second parameter to true for append mode
            val bufferedWriter = BufferedWriter(fileWriter)

            // Write the new data to the file followed by a newline character
            bufferedWriter.write(newData)
            bufferedWriter.newLine()

            // Close the BufferedWriter and FileWriter
            bufferedWriter.close()
            fileWriter.close()

            println("Data has been added to the file.")
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception, e.g., show an error message
        }
    }


    fun conditon() {
        val getDataFromSyncTable = eventDao.getDataFromSyncTable(RfidNo)
        val allDataFormScannedTable = eventDao.getAllScannedDetails(RfidNo)
        val selectedPosition = binding.spType.selectedItemPosition


//        if (select ==1 ){
//            if(getDataFromSyncTable.rfidNo== readHexString && getDataFromSyncTable.isfood== "true") {
//                Toast.makeText(this, readHexString, Toast.LENGTH_SHORT).show()
//                if (allDataFormScannedTable.isFood==1){
//                    alreadyExits()
//                } else{
//                    GlobalScope.launch {
//                        allDataFormScannedTable.isFood = 1
//                        eventDao.updateVehicleImage(allDataFormScannedTable)
//                    }
//                    //eventDao.allScannedDetails(AllScannedDetails(0, readHexString, 1, 0, 0)) }
//                    alertDialog(getDataFromSyncTable.name,getDataFromSyncTable.company,getDataFromSyncTable.image)
//                }
//
//
//            } else {
//                notAllowed()
//            }

//        if (selectedPosition==1){
//            if (getDataFromSyncTable.rfidNo== RfidNo && "getDataFromSyncTable.isLunch"=="true"){
//                if ("allDataFormScannedTable.isLunch"=="1"){
//                    alreadyExits()
//                } else if (allDataFormScannedTable==null){
//                    alertDialog("","","")
//
//                } else if (allDataFormScannedTable!=null){
//                    GlobalScope.launch {
//                        "allDataFormScannedTable.isLunch = 1"
//                       // eventDao.updateVehicleImage(allDataFormScannedTable)
//                    }
//                } else{
//                    notAllowed()
//                }
//            }
//        } else if (selectedPosition==2){
//            if (getDataFromSyncTable.rfidNo== RfidNo && "getDataFromSyncTable.isDinner"=="true"){
//                if ("allDataFormScannedTable.isDinner"=="1"){
//                    alreadyExits()
//                } else if (allDataFormScannedTable==null){
//                    alertDialog("","","")
//
//                } else if (allDataFormScannedTable!=null){
//                    GlobalScope.launch {
//                        "allDataFormScannedTable.isDinner = 1"
//                       // eventDao.updateVehicleImage(allDataFormScannedTable)
//                    }
//                } else{
//                    notAllowed()
//                }
//            }
//        } else if (selectedPosition==3){
//            if (getDataFromSyncTable.rfidNo== RfidNo && getDataFromSyncTable.ispowertimezone=="true"){
//                if (allDataFormScannedTable.IsPTZ==1){
//                    alreadyExits()
//                } else if (allDataFormScannedTable==null){
//                    alertDialog("","","")
//
//                } else if (allDataFormScannedTable!=null){
//                    GlobalScope.launch {
//                        allDataFormScannedTable.IsPTZ = 1
//                         eventDao.updateVehicleImage(allDataFormScannedTable)
//                    }
//                } else{
//                    notAllowed()
//                }
//            }
//        } else if (selectedPosition==4){
//            if (getDataFromSyncTable.rfidNo== RfidNo && getDataFromSyncTable.isexhibition=="true"){
//                alertDialog("","","")
//                GlobalScope.launch {
//                   // eventDao.allScannedDetails(AllScannedDetails(0, RfidNo, 1, 0, 0))
//                }
//            } else{
//                alertDialog("","","")
//            }
//        }
//
//
//    }


    }



}











