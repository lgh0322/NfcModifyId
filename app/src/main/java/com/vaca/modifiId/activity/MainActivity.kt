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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver


class MainActivity : AppCompatActivity(), BleViewAdapter.ItemClickListener {


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
                    if(size>=6){
                        if(this[0]==0xCC.toByte()){
                            if(CRCUtils.calCRC8(this)==this[size-1]){
                                when(this[1]){
                                    0xA1.toByte()->{
                                        ouputCard=ouputCard+"检测到的卡： "+mainX(this.copyOfRange(5,9))+"\n"
                                        binding.info.text=ouputCard
                                    }

                                    0xA2.toByte()->{
                                        var ouput="设备电量： "+this[5].toUByte().toInt().toString()+"%"
                                        binding.info.text=ouput
                                    }
                                    0xA3.toByte()->{

                                    }
                                    0xA4.toByte()->{
                                        var ouput="设备ID： "+mainX(this.copyOfRange(5,8))
                                        binding.info.text=ouput
                                    }


                                }
                            }
                        }
                    }




                    mainX(this)
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
            vibrator.vibrate(100);
            bleWorker.sendCmd(BleCmd.getAllCard())
        }

        binding.getDeviceID.setOnClickListener {
            vibrator.vibrate(100);
            bleWorker.sendCmd(BleCmd.getMachineId())
        }

        binding.closeLed.setOnClickListener {
            vibrator.vibrate(100);
            bleWorker.sendCmd(BleCmd.setIndicator(0))
        }

        binding.openLed.setOnClickListener {
            vibrator.vibrate(100);
            bleWorker.sendCmd(BleCmd.setIndicator(1))
        }


        binding.powerInfo.setOnClickListener {
            vibrator.vibrate(100);
            bleWorker.sendCmd(BleCmd.getPower())
        }


        binding.clearOuput.setOnClickListener {
            ouputCard=""
            binding.info.text=""
        }

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


