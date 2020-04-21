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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.meslmawy.ehlmdonations.HomeActivity
import com.meslmawy.ehlmdonations.R
import com.meslmawy.ehlmdonations.databinding.SignUpFragmentBinding
import com.meslmawy.ehlmdonations.models.State
import com.meslmawy.ehlmdonations.models.User


class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: SignUpFragmentBinding
    private lateinit var progresspar: ProgressbarLoader
    private var RuleName: String? = ""
    private var CommitteName: String? = ""

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
        setOnclickListeners()
        initRulesGroup()
        viewModel.state().observe(viewLifecycleOwner, Observer { event ->
            event?.let { state ->
                when (state.status) {
                    State.Status.LOADING ->
                        progresspar.showloader()
                    State.Status.SUCCESS -> {
                        progresspar.dismissloader()
                        moveToHomeActivity()
                    }
                    State.Status.ERROR -> {
                        progresspar.dismissloader()
                        viewModel.showToast(state.error?.message.toString())
                    }
                }
            }
        })
        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        })
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
                hideCommitteGroup()
            }
            else {
                val chip: Chip = chipGroup.findViewById(i)
                if (chip.tag as String == "Head" || chip.tag as String == "Member" || chip.tag as String == "Vice") {
                    RuleName = chip.tag as String
                    CommitteName = ""
                    showCommitteeGroup()
                    initCommitteesGroup()
                } else if (chip.tag as String == "President" || chip.tag as String == "Vice of President") {
                    RuleName = chip.tag as String
                    CommitteName = "EhlmTeam"
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
            }
            else {
                val chip: Chip = chipGroup.findViewById(i)
                CommitteName = chip.tag as String
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
        val name: String = binding.nameEdittext.text.toString().trim { it <= ' ' }
        val email: String = binding.emailEdittext.text.toString().trim { it <= ' ' }
        val password: String = binding.passwordEdittext.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(name) -> viewModel.showToast("fill Name field!!")
            TextUtils.isEmpty(email) -> viewModel.showToast("fill Email field!!")
            TextUtils.isEmpty(password) -> viewModel.showToast("fill Password field!!")
            RuleName?.isEmpty()!! -> viewModel.showToast("Choose Your rule in E7lm Team!!")
            CommitteName?.isEmpty()!! -> viewModel.showToast("Choose Your committee in E7lm Team!!")
            else -> {
                val user = User(name, email, password, RuleName, CommitteName)
                viewModel.signUp(user)
            }
        }
    }

    private fun moveToHomeActivity() {
        val i = Intent(activity, HomeActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        activity?.finish()
    }
}
