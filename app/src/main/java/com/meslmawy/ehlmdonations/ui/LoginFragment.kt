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
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.meslmawy.ehlmdonations.R
import com.meslmawy.ehlmdonations.databinding.LoginFragmentBinding


class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
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
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        progresspar = activity?.let { ProgressbarLoader(it) }!!
        setOnclickListeners()
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
        progresspar.showloader()
        val email: String = binding.emailEdittext.text.toString().trim { it <= ' ' }
        val password: String = binding.passwordEdittext.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            progresspar.dismissloader()
            Toast.makeText(activity, "fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    moveToHomeActivity()
                }.addOnFailureListener {
                    progresspar.dismissloader()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun moveToHomeActivity() {
        Toast.makeText(activity, "Login success..", Toast.LENGTH_SHORT)
            .show()
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
