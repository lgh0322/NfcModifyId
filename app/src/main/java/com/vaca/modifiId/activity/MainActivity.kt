package com.vaca.modifiId.activity

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.vaca.modifiId.adapter.BleViewAdapter
import com.vaca.modifiId.bean.BleBean
import com.vaca.modifiId.ble.BleCmd
import com.vaca.modifiId.ble.BleDataManager
import com.vaca.modifiId.ble.BleDataWorker
import com.vaca.modifiId.ble.BleScanManager
import com.vaca.modifiId.databinding.ActivityMainBinding
import com.vaca.modifiId.utils.CRCUtils
import com.vaca.modifiId.utils.StringUtil
import com.vaca.modifiId.utils.ZHexUtil
import com.vaca.modifiId.zxcxcv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver


class MainActivity : AppCompatActivity(), BleViewAdapter.ItemClickListener {
    companion object{
        var gg:Long=0
    }


    private val bleList: MutableList<BleBean> = ArrayList()


    private val dataScope = CoroutineScope(Dispatchers.IO)
    private lateinit var bleWorker: BleDataWorker
    private val scan = BleScanManager()
    private val mainVisible = MutableLiveData<Boolean>()


    lateinit var bleViewAdapter: BleViewAdapter

    fun updateDevice(byteArray: ByteArray) {
        dataScope.launch {
            bleWorker.updateDevice(byteArray)
        }

    }


    private fun startServer(app: Application) {
        scan.start()
        scan.setCallBack(object : BleScanManager.Scan {
            override fun scanReturn(name: String, bluetoothDevice: BluetoothDevice) {
                var z: Int = 0;
                for (ble in bleList) run {
                    if (ble.name == bluetoothDevice.name) {
                        z = 1
                    }
                }
                if (z == 0) {
                    bleList.add(BleBean(name, bluetoothDevice))
                    bleViewAdapter.addDevice(name, bluetoothDevice)
                }
            }
        })
    }

    lateinit var binding: ActivityMainBinding


    fun mainX(byteArray: ByteArray):String {
        var fuc=""
        for (b in byteArray) {
            val st = String.format("%02X", b)
            fuc+=("$st  ");
        }
       return fuc
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var ouputCard=""


        bleWorker = BleDataWorker(object : BleDataManager.OnNotifyListener {
            override fun onNotify(device: BluetoothDevice?, data: Data?) {
                data?.value?.run {
                    binding.info3.text="接收指令： "+mainX(this)
                    Log.e("fuck","接收指令： "+mainX(this))
                    binding.uiui.text="指令收发延时："+(System.currentTimeMillis()-gg).toString()+" ms"
                    val data=this!!
                    if(this.size>10){
                        val valueArr = byteArrayOf(data.get(10), data.get(9))
                        val strValue: String = ZHexUtil.encodeHexStr(valueArr) //10+9 Convert to decimal

                        val value = Integer.valueOf(strValue, 16)
                        val valueStr: String = StringUtil.formatTo1(value.toDouble() / 18)
                        Log.e("fuck2",valueStr)
                    }


                }
            }

        })








        bleViewAdapter = BleViewAdapter(this)
        binding.bleTable.adapter = bleViewAdapter
        binding.bleTable.layoutManager = GridLayoutManager(this, 2);
        bleViewAdapter.setClickListener(this)


        mainVisible.observe(this, {
            if (it) {
                binding.mainOperate.visibility = View.VISIBLE
                binding.bleTable2.visibility = View.GONE
            } else {
                binding.mainOperate.visibility = View.GONE
                binding.bleTable2.visibility = View.VISIBLE
            }

        })

        Thread {
            scan.initScan(application)
            startServer(application)
        }.start()


        //------------------------button control
        val vibrator = this.getSystemService(VIBRATOR_SERVICE) as Vibrator

        binding.getAllCard.setOnClickListener {

            sendCmd(BleCmd.getAllCard())
            vibrator.vibrate(100);
        }

        binding.getDeviceID.setOnClickListener {
            vibrator.vibrate(100);
            sendCmd(BleCmd.getMachineId())
        }

        binding.closeLed.setOnClickListener {
            vibrator.vibrate(100);
           sendCmd(BleCmd.setIndicator(0))
        }

        binding.openLed.setOnClickListener {
            vibrator.vibrate(100);
            sendCmd(BleCmd.setIndicator(1))
        }


        binding.powerInfo.setOnClickListener {
            vibrator.vibrate(100);
           sendCmd(BleCmd.getPower())
        }

        binding.testMode.setOnClickListener {
            vibrator.vibrate(100);
            sendCmd(zxcxcv.fuck2())
        }


        binding.clearOuput.setOnClickListener {
            ouputCard=""
            binding.info.text=""
            sendCmd(zxcxcv.fuck())
        }

    }

    fun sendCmd(b:ByteArray){
        binding.info2.text="发送指令： "+mainX(b)
        bleWorker.sendCmd(b)
    }


    override fun onScanItemClick(bluetoothDevice: BluetoothDevice?) {

        bleWorker.initWorker(application, bluetoothDevice, object : ConnectionObserver {
            override fun onDeviceConnecting(device: BluetoothDevice) {
                binding.connectState.text = "蓝牙连接中"
            }

            override fun onDeviceConnected(device: BluetoothDevice) {
                binding.connectState.text = "蓝牙已连接"
            }

            override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
                binding.connectState.text = "蓝牙连接失败"
            }

            override fun onDeviceReady(device: BluetoothDevice) {
                binding.connectState.text = "蓝牙设备已就绪"
            }

            override fun onDeviceDisconnecting(device: BluetoothDevice) {
                binding.connectState.text = "蓝牙断开中"
            }

            override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
                binding.connectState.text = "蓝牙已断开"
            }

        })
        mainVisible.postValue(true)
    }


}


