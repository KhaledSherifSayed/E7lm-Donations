package com.meslmawy.ehlmdonations.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meslmawy.ehlmdonations.R
import com.meslmawy.ehlmdonations.databinding.SignUpFragmentBinding
import com.meslmawy.ehlmdonations.models.User


class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: SignUpFragmentBinding
    private lateinit var progresspar: ProgressbarLoader
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var RuleName: String? = null
    private var CommitteName: String? = null

    companion object {
        fun newInstance() = SignUpFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignUpFragmentBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        progresspar = activity?.let { ProgressbarLoader(it) }!!
        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)
        initFirebaseElements()
        setOnclickListeners()
        initRulesGroup()
        return binding.root
    }

    private fun setOnclickListeners() {
        binding.signupButton.setOnClickListener {
            signuplistner()
        }
        binding.signtologTxt.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initFirebaseElements() {
        firebaseAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
    }

    private fun initRulesGroup() {
        // Create a Chip for each regionsList item.
        val chipGroup = binding.ruleList
        val inflator = LayoutInflater.from(chipGroup.context)
        val Rules = arrayListOf<String>(*resources.getStringArray(R.array.RulesNames))
        val children = Rules.map { ruleName ->
            val chip = inflator.inflate(R.layout.region, chipGroup, false) as Chip
            chip.text = ruleName
            chip.tag = ruleName
            chip
        }
        chipGroup.removeAllViews()
        for (chip in children) {
            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedChangeListener { chipGroup, i ->
            if(i == -1){
                RuleName = ""
                Toast.makeText(requireContext(), RuleName, Toast.LENGTH_SHORT).show()
                hideCommitteGroup()
            }
            else {
                val chip: Chip = chipGroup.findViewById(i)
                if (chip.tag as String == "Head" || chip.tag as String == "Member" || chip.tag as String == "Vice") {
                    RuleName = chip.tag as String
                    Toast.makeText(requireContext(), RuleName, Toast.LENGTH_SHORT).show()
                    showCommitteeGroup()
                    initCommitteesGroup()
                } else if (chip.tag as String == "President" || chip.tag as String == "Vice of President") {
                    RuleName = chip.tag as String
                    CommitteName = "EhlmTeam"
                    Toast.makeText(requireContext(), RuleName, Toast.LENGTH_SHORT).show()
                    hideCommitteGroup()
                }
            }
        }
    }

    private fun initCommitteesGroup() {
        // Create a Chip for each regionsList item.
        val chipGroup = binding.committiesList
        val inflator = LayoutInflater.from(chipGroup.context)
        val committees = arrayListOf<String>(*resources.getStringArray(R.array.Committees))
        val children = committees.map { committeName ->
            val chip = inflator.inflate(R.layout.region, chipGroup, false) as Chip
            chip.text = committeName
            chip.tag = committeName
            chip
        }
        chipGroup.removeAllViews()
        for (chip in children) {
            chipGroup.addView(chip)
        }
        chipGroup.setOnCheckedChangeListener { chipGroup, i ->
            if(i == -1){
                CommitteName = ""
                Toast.makeText(requireContext(), CommitteName, Toast.LENGTH_SHORT).show()
            }
            else {
                val chip: Chip = chipGroup.findViewById(i)
                CommitteName = chip.tag as String
                Toast.makeText(requireContext(), CommitteName, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCommitteeGroup() {
        binding.CommitelistScrollview.visibility = View.VISIBLE
    }

    private fun hideCommitteGroup() {
        binding.CommitelistScrollview.visibility = View.GONE
    }

    private fun signuplistner() {
        progresspar.showloader()
        val name: String = binding.nameEdittext.text.toString().trim { it <= ' ' }
        val email: String = binding.emailEdittext.text.toString().trim { it <= ' ' }
        val password: String = binding.passwordEdittext.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
            RuleName?.isEmpty()!! || CommitteName?.isEmpty()!!
        ) {
            progresspar.dismissloader()
            Toast.makeText(activity, "fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            val user = User(name, email, password, RuleName, CommitteName)
            createUserInFireBase(user)
        }
    }

    private fun moveToHomeActivity() {
        val i = Intent(activity, HomeActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        activity?.finish()
    }

    private fun createUserInFireBase(user: User) {
        user.email?.let {
            user.password?.let { it1 ->
                firebaseAuth.createUserWithEmailAndPassword(
                    it,
                    it1
                ).addOnSuccessListener {
                    val userid = firebaseAuth.currentUser!!.uid
                    user.id = userid
                    val users = mFirestore.collection("Users")
                    // Add a new document with a generated ID
                    user.id?.let {
                        users.document(it).set(user)
                        moveToHomeActivity()
                    }
                }.addOnFailureListener {
                    progresspar.dismissloader()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
