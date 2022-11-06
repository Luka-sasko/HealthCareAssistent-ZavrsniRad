package com.example.healthcareassistent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), PersonalInformationsComunicator, CaloriesIntakeComunicator{

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userID=intent.getStringExtra("user_id")
        val date=SimpleDateFormat("dd/M/yyyy")
        val currentDate=date.format(Date())
        val db= FirebaseFirestore.getInstance()
        val docRefUsers = db.collection("Users").document(userID.toString())

        docRefUsers.get().addOnSuccessListener {
            val data=it.data
            val oldDate=data?.get("datum").toString()

            if (it["ime"]!!.equals("Ime")){
                replaceFragment(EditPersonalInformationsragment())
                Toast.makeText(this,"Unesite podatke!",Toast.LENGTH_SHORT).show()
            }

            if (oldDate != currentDate) {
                db.runTransaction { transaction ->
                    transaction.update(docRefUsers, "datum", currentDate)
                    transaction.update(docRefUsers, "kolicinaVoda", 0)
                }
            }
        }

       drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_PersonalInfo -> replaceFragment(PersonalInformationsFragment())
                R.id.nav_CaloriesIntake -> replaceFragment(CaloriesIntakeFragment())
                R.id.nav_CaloriesBurned -> replaceFragment(BurnedCaloriesFragment())
                R.id.nav_FluidIntake -> replaceFragment(FluidIntakeFragment())
                R.id.nav_Bmi -> replaceFragment(BmiFragment())
                R.id.nav_Glucose -> replaceFragment(GlucoseLevelFragment())
                R.id.nav_BloodPresure -> replaceFragment(BloodPresureFragment())
                R.id.nav_kolicinaSNa -> replaceFragment(KolicinaSnaFragment())
                R.id.nav_odjava-> signOut()
            }
            true
        }

        docRefUsers.get().addOnSuccessListener {
            val ime =it["ime"].toString()
            findViewById<TextView>(R.id.tvWelcome).text="Dobro došli, "+ime+"!"
            val infoVisinaTezina= "Trenutna kilaža: "+it["težina"].toString()+
                    "kg, visina: "+it["visina"].toString()+"cm"
            findViewById<TextView>(R.id.tvPorukaVisinaTezina).text=infoVisinaTezina
        }

        docRefUsers.collection("GlucoseLevel").get().addOnSuccessListener {
            var suma=0.0
            var prosjek=0.0
            var n=0
            for(data in it){
                n++
                if(data["Šećer"]!=null){
                    suma=suma+data["Šećer"].toString().toDouble()
                    prosjek=(((suma/n)*10).roundToInt().toDouble())/10
                }
            }
            val tvSecer=findViewById<TextView>(R.id.tvPorukaŠećer)
            tvSecer.text="Posječna vrijednost šećera u krvi iznosi "+prosjek.toString()+" mmol/L"
            if (prosjek>10){tvSecer.setTextColor(getResources().getColor(R.color.orange))}
            if (prosjek<4){tvSecer.setTextColor(getResources().getColor(R.color.red))}

        }

        docRefUsers.collection("BloodPresure").orderBy("Datum").limit(1).get().addOnSuccessListener {
            for(data in it){
                findViewById<TextView>(R.id.tvPorukaTlak).text="Posljednja vrijednost tlaka iznosi "+data["Tlak"].toString()
            }
        }
        docRefUsers.collection("Calories").document("podaci").get().addOnSuccessListener {
            val infoCilj="Vaš trenutni dnevni cilj za unos kalorija je "+it["Cilj"].toString()+"kcal!\n"+
                    "Trenutni plan prehrane iznosi: UH:"+it["UGHCilj"].toString()+"g, Proteina:"+it["ProteiniCilj"].toString()+
                    "g, Masti"+it["ProteiniCilj"].toString()+"g."
            val infoUneseno="Danas uneseno UH:"+it["UGH"].toString()+"g, "+"Proteina: "+it["Proteini"].toString()+"g, "+
                    "Masti: "+it["Masti"].toString()+"g."
            val infoUnesenoJucer="Jučer  uneseno UH:"+it["UGHJucer"].toString()+"g, "+"Proteina: "+it["ProteiniJucer"].toString()+"g, "+
            "Masti: "+it["MastiJucer"].toString()+"g."
            findViewById<TextView>(R.id.tvPorukaCilj).text=infoCilj
            findViewById<TextView>(R.id.tvUnesenoPoruka).text=infoUneseno+"\n"+infoUnesenoJucer+"\n"

        }
    }

    override fun onBackPressed() {

    }


    private fun signOut() {
        Toast.makeText(this@MainActivity, "Uspješno ste se odjavili!", Toast.LENGTH_LONG).show()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        finish()
    }

    private fun replaceFragment(fragment: Fragment){
        val bundle = Bundle()
        bundle.putString("uid", intent.getStringExtra("user_id"))

        fragment.arguments=bundle
        drawerLayout.closeDrawers()

        val fragmentMenager = supportFragmentManager
        val fragmentTransaction=fragmentMenager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
            .commit()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    override fun changeEditingFragment() {
        Toast.makeText(applicationContext,"Izmjena podataka",Toast.LENGTH_SHORT).show()
        replaceFragment(EditPersonalInformationsragment(),)
    }

    override fun changeFragment() {
        Toast.makeText(applicationContext,"Spremljeno!",Toast.LENGTH_SHORT).show()
        replaceFragment(PersonalInformationsFragment(),)
    }

    override fun refreshFragment() {
        replaceFragment(CaloriesIntakeFragment())
    }

}