package com.meslmawy.ehlmdonations

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.meslmawy.ehlmdonations.databinding.ActivityHomeBinding
import com.meslmawy.ehlmdonations.models.User


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityHomeBinding
    var firebaseUser: FirebaseUser? = null
    var firebaseAuth: FirebaseAuth? = null
    var navController: NavController? = null
    var current_uid: String? = null
    var  mFirestore : FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.currentUser
        navController = Navigation.findNavController(this, R.id.nav_home_fragment)
        current_uid = firebaseUser?.uid
        mFirestore = FirebaseFirestore.getInstance()
        setupNavigation()
        getHeaderData()
    }

    /**
     * Called when the hamburger menu or back button are pressed on the Toolbar
     * Delegate this to Navigation.
     */
    override fun onSupportNavigateUp() =
        navigateUp(findNavController(R.id.nav_home_fragment), binding.drawerLayout)


    /**
     * Setup Navigation for this Activity
     */
    private fun setupNavigation() {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // first find the nav controller
        val navController = findNavController(R.id.nav_home_fragment)
        // then setup the action bar, tell it about the DrawerLayout
        setupActionBarWithNavController(navController, binding.drawerLayout)
        // finally setup the left drawer (called a NavigationView)
        binding.navigationView.setupWithNavController(navController)
        // setup item_listeners on navigation view
        binding.navigationView.setNavigationItemSelectedListener(this)

        /*val actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, (R.string.open), (R.string.close))
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()*/

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            when (destination.id) {
                R.id.home -> {
                    toolBar.setDisplayShowTitleEnabled(false)
                }
                else -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nev_home -> binding.drawerLayout.closeDrawer(GravityCompat.START)
            R.id.nev_profile -> navController?.navigate(R.id.profileFragment)
            R.id.nev_logout -> if (firebaseUser != null) {
                firebaseAuth?.signOut()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                (this).overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun getHeaderData(){
        val header = binding.navigationView.getHeaderView(0)
        val nametextView = header.findViewById<TextView>(R.id.header_name_text)
        val emailtextView = header.findViewById<TextView>(R.id.header_email_text)
        val nameimageView = header.findViewById<TextView>(R.id.profile_image)
        val userdocData = current_uid?.let { mFirestore?.collection("Users")?.document(it) }
        userdocData?.get()?.addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)
            if (user != null) {
                val firstName =  "Hi ${user.firstName + " " + user.lastName}"
                val abbrevName = user.firstName?.get(0).toString() + user.lastName?.get(0).toString()
                nametextView.text = firstName
                emailtextView.text = user.email
                nameimageView.text = abbrevName
            }
        }?.addOnFailureListener {
            Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

}
