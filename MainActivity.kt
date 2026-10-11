package com.majnu.dost

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.IntentFilter
import android.content.Context
import android.os.BatteryManager
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var status: TextView
    private lateinit var heard: TextView
    private lateinit var tts: TextToSpeech
    private lateinit var input: EditText
    private val voiceRequestCode = 701
    private val micPermissionRequestCode = 702
    private val assistantRoleRequestCode = 703

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildUi()
        status.text = "Ready — DOST ko default assistant set kar sakte ho"
        if (intent.getBooleanExtra("start_voice", false)) {
            window.decorView.post { startVoiceInput() }
        }
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) tts.language = Locale("hi", "IN")
    }

    private fun buildUi() {
        val scroll = ScrollView(this)
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(24), dp(22), dp(24))
            setBackgroundColor(0xFF101827.toInt())
        }
        scroll.addView(col)
        setContentView(scroll)
        col.addView(label("🤖", 46f, false, Gravity.CENTER))
        col.addView(label("DOST AI", 30f, true, Gravity.CENTER))
        col.addView(label("Android assistant starter · Hindi/Hinglish", 14f, false, Gravity.CENTER))
        status = label("Starting…", 15f, true, Gravity.CENTER).apply {
            setPadding(dp(12), dp(14), dp(12), dp(14))
            setBackgroundColor(0xFF1E293B.toInt())
        }
        col.addView(status, LinearLayout.LayoutParams(-1, -2).apply { topMargin=dp(18); bottomMargin=dp(14) })
        col.addView(button("⭐ DOST ko Default Assistant banao") { requestAssistantRole() })
        col.addView(button("🎙 Mic se command bolo") { startVoiceInput() }.apply {
            val p=LinearLayout.LayoutParams(-1,-2); p.topMargin=dp(8); layoutParams=p
        })
        col.addView(label("Maine kya suna:", 16f, true, Gravity.START).apply { setPadding(0,dp(16),0,dp(6)) })
        heard = label("Abhi koi command nahi", 15f, false, Gravity.START).apply {
            setPadding(dp(12),dp(14),dp(12),dp(14)); setBackgroundColor(0xFF0F172A.toInt())
        }
        col.addView(heard)
        col.addView(label("Quick commands", 17f, true, Gravity.START).apply { setPadding(0,dp(18),0,dp(6)) })
        listOf(
            "▶ YouTube kholo" to { runCommand("YouTube kholo") },
            "💬 WhatsApp kholo" to { runCommand("WhatsApp kholo") },
            "🗺 Maps kholo" to { runCommand("Maps kholo") },
            "🔎 Google kholo" to { runCommand("Google kholo") },
            "🕒 Time batao" to { runCommand("time batao") },
            "📅 Aaj ki date batao" to { runCommand("aaj ki date") },
            "🔋 Battery batao" to { runCommand("battery batao") },
            "📞 Dialer kholo" to { runCommand("dialer kholo") },
            "⏰ Alarm app kholo" to { runCommand("alarm kholo") },
            "⚙ Phone Settings kholo" to { runCommand("settings kholo") },
            "📶 Bluetooth settings" to { runCommand("bluetooth settings") },
            "📡 Wi-Fi settings" to { runCommand("wifi settings") },
            "👥 Contacts kholo" to { runCommand("contacts kholo") },
            "🎵 Music app kholo" to { runCommand("music kholo") }
        ).forEach { (txt, action) ->
            col.addView(button(txt, action).apply {
                val p=LinearLayout.LayoutParams(-1,-2); p.topMargin=dp(7); layoutParams=p
            })
        }
        input = EditText(this).apply {
            hint="Command type karo…"; setHintTextColor(0xFF94A3B8.toInt())
            setTextColor(0xFFFFFFFF.toInt()); setPadding(dp(12),dp(12),dp(12),dp(12)); setSingleLine(true)
        }
        col.addView(input, LinearLayout.LayoutParams(-1,-2).apply { topMargin=dp(16) })
        col.addView(button("Command chalao") { runCommand(input.text.toString()) }.apply {
            val p=LinearLayout.LayoutParams(-1,-2); p.topMargin=dp(7); layoutParams=p
        })
        col.addView(label(
            "DOST v0.4: Hindi voice input, speech replies, app shortcuts, time/date, battery, dialer, alarms, " +
            "search aur settings. Default assistant ke liye Android confirmation chahiye. " +
            "Custom always-listening wake word, voice biometric unlock, cloud AI backend aur watch companion " +
            "abhi included nahi hain; inke liye separate tested implementation/infrastructure chahiye. " +
            "UPI/banking actions intentionally blocked hain.",
            12f, false, Gravity.START
        ).apply { setTextColor(0xFFCBD5E1.toInt()); setPadding(0,dp(20),0,0) })
    }

    private fun requestAssistantRole() {
        val rm = getSystemService(RoleManager::class.java)
        if (rm == null || !rm.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) {
            speak("Is Android build par assistant role available nahi hai. Settings mein Default apps aur Digital assistant check karo.")
            return
        }
        if (rm.isRoleHeld(RoleManager.ROLE_ASSISTANT)) {
            speak("DOST pehle se default assistant hai.")
            return
        }
        try { startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT), assistantRoleRequestCode) }
        catch (_: Exception) { speak("Default assistant request nahi khul paayi. Android Settings mein Default apps check karo.") }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == assistantRoleRequestCode) {
            val rm = getSystemService(RoleManager::class.java)
            speak(if (rm?.isRoleHeld(RoleManager.ROLE_ASSISTANT) == true)
                "DOST default assistant set ho gaya. Ab phone ke assistant shortcut se khol kar test karo."
            else "Default assistant change nahi hua. Android prompt mein DOST ko select karna zaroori hai.")
        } else if (requestCode == voiceRequestCode && resultCode == RESULT_OK) {
            runCommand(data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty())
        }
    }

    private fun startVoiceInput() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), micPermissionRequestCode); return
        }
        try {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "DOST ko command bolo")
            }
            startActivityForResult(i, voiceRequestCode)
        } catch (_: ActivityNotFoundException) {
            speak("Voice recognition service nahi mili. Command type karke try karo.")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == micPermissionRequestCode && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) startVoiceInput()
        else if (requestCode == micPermissionRequestCode) speak("Voice ke liye microphone permission zaroori hai.")
    }

    private fun runCommand(raw: String) {
        val s=raw.trim()
        if(s.isBlank()){speak("Pehle command bolo ya type karo.");return}
        heard.text=s
        val q=s.lowercase(Locale.ROOT)
        when {
            q.contains("time") || q.contains("समय") || q.contains("टाइम") || q.contains("kitne baje") || q.contains("वक्त") ->
                speak("Abhi "+SimpleDateFormat("h:mm a", Locale("hi","IN")).format(Date())+" baj rahe hain.")
            q.contains("date") || q.contains("तारीख") || q.contains("दिनांक") || q.contains("aaj ki date") || q.contains("आज की तारीख") ->
                speak("Aaj ki tareekh "+SimpleDateFormat("d MMMM yyyy", Locale("hi","IN")).format(Date())+" hai.")
            q.contains("battery") || q.contains("बैटरी") || q.contains("charge kitna") || q.contains("चार्ज कितना") -> {
                val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
                if (level >= 0 && scale > 0) speak("Phone ki battery "+(level * 100 / scale)+" percent hai.")
                else speak("Battery status abhi nahi mil paaya.")
            }
            q.contains("dialer") || q.contains("keypad") || q.contains("number milao") || q.contains("फोन लगाओ") || q.contains("phone lagao") -> {
                try { startActivity(Intent(Intent.ACTION_DIAL)); speak("Phone dialer khol raha hoon. Number check karke khud call button dabana.") }
                catch (_: Exception) { speak("Dialer nahi khul paaya.") }
            }
            q.contains("youtube") -> openAppOrUrl("com.google.android.youtube","https://www.youtube.com","YouTube")
            q.contains("whatsapp") -> openAppOrUrl("com.whatsapp","https://wa.me/","WhatsApp")
            q.contains("maps") || q.contains("map") || q.contains("नक्शा") -> openAppOrUrl("com.google.android.apps.maps","https://maps.google.com","Maps")
            q.contains("alarm") || q.contains("अलार्म") -> {
                try { startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS)); speak("Clock aur alarm khol raha hoon.") }
                catch (_:Exception) { speak("Alarm app nahi khul paayi.") }
            }
            q.contains("contacts") || q.contains("contact") || q.contains("संपर्क") -> {
                try { startActivity(Intent(Intent.ACTION_VIEW, android.provider.ContactsContract.Contacts.CONTENT_URI)); speak("Contacts khol raha hoon.") }
                catch (_: Exception) { speak("Contacts app nahi khul paayi.") }
            }
            q.contains("music") || q.contains("gaana") || q.contains("गाना") || q.contains("म्यूजिक") -> {
                try {
                    val i = packageManager.getLaunchIntentForPackage("com.google.android.apps.youtube.music")
                        ?: packageManager.getLaunchIntentForPackage("com.spotify.music")
                    if (i != null) { startActivity(i); speak("Music app khol raha hoon.") }
                    else openUrl("https://music.youtube.com", "Music app nahi mili, music website khol raha hoon.")
                } catch (_: Exception) { openUrl("https://music.youtube.com", "Music khol raha hoon.") }
            }
            q.contains("bluetooth") || q.contains("ब्लूटूथ") -> {
                try { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)); speak("Bluetooth settings khol raha hoon.") }
                catch (_: Exception) { speak("Bluetooth settings nahi khul paayi.") }
            }
            q.contains("wifi") || q.contains("wi-fi") || q.contains("वाईफाई") -> {
                try { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)); speak("Wi-Fi settings khol raha hoon.") }
                catch (_: Exception) { speak("Wi-Fi settings nahi khul paayi.") }
            }
            q.contains("settings") || q.contains("सेटिंग") -> {
                try { startActivity(Intent(Settings.ACTION_SETTINGS)); speak("Phone settings khol raha hoon.") }
                catch (_:Exception) { speak("Settings nahi khul paayi.") }
            }
            q.contains("google") && (q.contains("search") || q.contains("खोज") || q.contains("ढूंढ")) -> {
                val term=s.replace(Regex("(?i)google|search|पर|खोजो|ढूंढो|करो"),"").trim()
                openUrl("https://www.google.com/search?q="+Uri.encode(term.ifBlank{s}),"Google par search kar raha hoon.")
            }
            q.contains("google") -> openUrl("https://www.google.com","Google khol raha hoon.")
            q.contains("search") || q.contains("खोज") || q.contains("ढूंढ") -> {
                val term=s.replace(Regex("(?i)search|खोजो|ढूंढो|करो"),"").trim()
                openUrl("https://www.google.com/search?q="+Uri.encode(term),"Search kar raha hoon.")
            }
            q.contains("help") || q.contains("madad") || q.contains("मदद") || q.contains("commands") ->
                speak("Main YouTube, WhatsApp, Maps, Google search, time, date, battery, dialer, alarm, contacts, music, Bluetooth, Wi-Fi aur settings khol sakta hoon.")
            q.contains("upi") || q.contains("bank") || q.contains("payment") || q.contains("पैसे भेज") || q.contains("यूपीआई") ->
                speak("Security ke liye main UPI ya banking transaction control nahi karta.")
            else -> speak("Main abhi offline command assistant hoon, full AI chat brain nahi. Help bolo available commands sunne ke liye.")
        }
    }

    private fun openAppOrUrl(pkg:String,url:String,name:String) {
        try {
            val i=packageManager.getLaunchIntentForPackage(pkg)
            if(i!=null){startActivity(i);speak("$name khol raha hoon.")}
            else openUrl(url,"$name app nahi mili, website khol raha hoon.")
        } catch (_:Exception) { openUrl(url,"$name khol raha hoon.") }
    }
    private fun openUrl(url:String,msg:String) {
        speak(msg); try{startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))}
        catch(_:Exception){Toast.makeText(this,"Link nahi khul paaya",Toast.LENGTH_SHORT).show()}
    }
    private fun speak(text:String) {
        status.text=text
        if(::tts.isInitialized) tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"dost_reply")
    }
    private fun label(text:String,size:Float,bold:Boolean,gravity:Int)=TextView(this).apply{
        this.text=text;textSize=size;this.gravity=gravity;setTextColor(0xFFF8FAFC.toInt())
        if(bold)setTypeface(typeface,android.graphics.Typeface.BOLD)
    }
    private fun button(text:String,action:()->Unit)=Button(this).apply{
        this.text=text;isAllCaps=false;setTextColor(0xFFFFFFFF.toInt())
        setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2563EB.toInt()))
        setOnClickListener{action()}
    }
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
    override fun onDestroy(){if(::tts.isInitialized){tts.stop();tts.shutdown()};super.onDestroy()}
}
