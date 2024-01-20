package com.example.androidcoroutines

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.androidcoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding.btn.setOnClickListener {

            add()
        }
        // 创建一个CoroutineScope与Activity生命周期绑定


    }
    private val mutex = Mutex()
    fun add(){
        val scope = lifecycleScope
        a++;
        val currentNum:Int =a
        scope.launch(Dispatchers.IO) {

            mutex.withLock {
                // 第一个任务
                val resultOne = withContext(Dispatchers.IO) {
                    delay(2000L) // 模拟耗时操作
                    "1 "
                }

                withContext(Dispatchers.Main) {
                    binding.textview.text = "Task 1 finished:  $currentNum "
                }

                // 第二个任务，基于第一个任务的结果
                val resultTwo = withContext(Dispatchers.IO) {
                    delay(2000L) // 模拟耗时操作
                    "2"
                }

                withContext(Dispatchers.Main) {
                    binding.textview.text = ("\nTask 2 finished:  $currentNum")
                }

                val resultTw3 = withContext(Dispatchers.IO) {
                    delay(3000L) // 模拟耗时操作
                    "3"
                }

                withContext(Dispatchers.Main) {
                    binding.textview.text = ("\nTask 3 finished:  $currentNum")
                }
            }
        }
    }
    var a:Int=0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}