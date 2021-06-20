package com.vaca.modifiId.activity

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.os.Bundle
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        bleWorker = BleDataWorker(object : BleDataManager.OnNotifyListener {
            override fun onNotify(device: BluetoothDevice?, data: Data?) {
                data?.value?.run {
                    Log.e("fuck", String(this))

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


        binding.getAllCard.setOnClickListener {
            bleWorker.sendCmd(BleCmd.getAllCard())
        }

        binding.getDeviceID.setOnClickListener {
            bleWorker.sendCmd(BleCmd.getMachineId())
        }

        binding.closeLed.setOnClickListener {
            bleWorker.sendCmd(BleCmd.setIndicator(0))
        }

        binding.openLed.setOnClickListener {
            bleWorker.sendCmd(BleCmd.setIndicator(1))
        }


        binding.powerInfo.setOnClickListener {
            bleWorker.sendCmd(BleCmd.getPower())
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


