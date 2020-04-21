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
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.meslmawy.ehlmdonations.HomeActivity
import com.meslmawy.ehlmdonations.R
import com.meslmawy.ehlmdonations.databinding.LoginFragmentBinding
import com.meslmawy.ehlmdonations.models.State


class LoginFragment : Fragment() {


    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }
    private lateinit var binding: LoginFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progresspar: ProgressbarLoader

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginFragmentBinding.inflate(inflater)
        binding.viewmodel = viewModel
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        firebaseAuth = FirebaseAuth.getInstance()
        progresspar = activity?.let { ProgressbarLoader(it) }!!
        setOnclickListeners()

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
        binding.loginButton.setOnClickListener {
            loginListener()
        }

        binding.logtosignup.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(LoginFragmentDirections.actionDeafulLoginToSignUpFragment())
        }
    }

    private fun loginListener() {
        val email: String = binding.emailEdittext.text.toString().trim { it <= ' ' }
        val password: String = binding.passwordEdittext.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(email) -> viewModel.showToast("fill email field")
            TextUtils.isEmpty(password) -> viewModel.showToast("fill password field")
            else -> {
                viewModel.signIn(email,password)
            }
        }
    }

    private fun moveToHomeActivity() {
        val i = Intent(activity, HomeActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        activity?.finish()
    }

    override fun onStart() {
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(activity, HomeActivity::class.java))
            activity?.finish()
        }
        super.onStart()
    }
}
