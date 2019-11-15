package com.autohub.loginsignup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import com.autohub.loginsignup.databinding.ActivityLoginBinding
import com.autohub.loginsignup.student.SignupStart
import com.autohub.skln.BaseActivity
import com.autohub.skln.BuildConfig
import com.autohub.skln.utills.ActivityUtils
import com.autohub.skln.utills.AppConstants
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.firebase.auth.EmailAuthProvider

/**
 * Created by Vt Netzwelt
 */
class LoginActivity : BaseActivity() {
    private var mBinding: ActivityLoginBinding? = null
    private val mAccountType = AppConstants.TYPE_STUDENT
    private lateinit var manager: SplitInstallManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        manager = SplitInstallManagerFactory.create(this)
        mBinding!!.callback = this

        mBinding!!.usertype.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radiostudent) {
                updateUi(true)
            } else {
                updateUi(false)
            }
        }

    }


    private fun updateUi(isStudent: Boolean) {
        if (isStudent) {
            mBinding!!.tvHintemamil.visibility = View.VISIBLE
            mBinding!!.rremail.visibility = View.VISIBLE
            mBinding!!.tvHintloginid.visibility = View.GONE
            mBinding!!.rrloginid.visibility = View.GONE
            mBinding!!.tvForgotPassword.text = resources.getString(R.string.forgot_pass)

        } else {

            mBinding!!.tvForgotPassword.text = resources.getString(R.string.forgot_passid)
            mBinding!!.tvHintemamil.visibility = View.GONE
            mBinding!!.rremail.visibility = View.GONE
            mBinding!!.tvHintloginid.visibility = View.VISIBLE
            mBinding!!.rrloginid.visibility = View.VISIBLE
        }

    }

    fun login() {
        if (!mBinding!!.radiostudent.isChecked) {
            Toast.makeText(this, "Tutor Verified!", Toast.LENGTH_SHORT).show()
            loadAndLaunchModule(TUTOR_FEATURE, "tutormodule")
        }

        val email = mBinding!!.edtemail.text
        if (email == null) {
            mBinding!!.edtemail.error = resources.getString(R.string.enter_email)
            mBinding!!.edtemail.requestFocus()
            return
        }

        if (!isValidEmailId(email.toString())) {
            mBinding!!.edtemail.error = resources.getString(R.string.enter_validemail)
            mBinding!!.edtemail.requestFocus()
            return
        }


        val password = getString(mBinding!!.edtPassword.text)
        if (TextUtils.isEmpty(password)) {
            mBinding!!.edtPassword.error = resources.getString(R.string.enter_password)
            mBinding!!.edtPassword.requestFocus()
            // showSnackError(R.string.enter_password)
            return
        }
        showLoading()


        validateUserCredentials()

    }

    private fun validateUserCredentials() {

        val credential = EmailAuthProvider.getCredential(mBinding!!.edtemail.text.toString().trim(),
                /*encrypt(*/mBinding!!.edtPassword.text.toString().trim()/*)*/)

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    hideLoading()
                    if (it.isSuccessful) {
                        saveUserIdLocally()
                    } else {
                        showSnackError(it.exception.toString())
                    }
                }.addOnFailureListener {
                    showSnackError(it.message)
                    hideLoading()

                }
    }

    private fun updatePasswordVisibility(editText: AppCompatEditText) {
        if (editText.transformationMethod is PasswordTransformationMethod) {
            editText.transformationMethod = null
            mBinding!!.txtshowpass.setText(R.string.hide)
        } else {
            editText.transformationMethod = PasswordTransformationMethod()
            mBinding!!.txtshowpass.setText(R.string.show)

        }
        editText.setSelection(editText.length())
    }


    private fun moveNext() {


        if (mBinding!!.radiostudent.isChecked) {
            Toast.makeText(this, "Student Verified!", Toast.LENGTH_SHORT).show()
            appPreferenceHelper.setSignupComplete(true)
            loadAndLaunchModule(STUDENT_FEATURE, "studentmodule")
        } else {
            Toast.makeText(this, "Tutor Verified!", Toast.LENGTH_SHORT).show()
            loadAndLaunchModule(TUTOR_FEATURE, "tutormodule")

        }

    }

    private fun saveUserIdLocally() {
        firebaseStore.collection(getString(R.string.db_root_students)).whereEqualTo(AppConstants.KEY_USER_ID, firebaseAuth.currentUser!!.uid)
                .get().addOnSuccessListener {
                    it.forEach {
                        getAppPreferenceHelper().setUserId(it.id)
                        moveNext()
                    }
                }

    }

    /*
        * Load the Student/Tutor Module
        * */
    private fun loadAndLaunchModule(name: String, feature_name: String) {
        if (manager.installedModules.contains(feature_name)) {
            Intent().setClassName(BuildConfig.APPLICATION_ID, name)
                    .also {
                        startActivity(it)
                        finishAffinity()
                    }
            return
        } else {
            showLoading()
        }

        val request = SplitInstallRequest.newBuilder()
                .addModule(feature_name)
                .build()

        manager.startInstall(request).addOnSuccessListener {
            hideLoading()
            finishAffinity()
        }.addOnFailureListener {
            showSnackError(it.message)
            hideLoading()
        }
    }

    fun signUp() {
        if (mBinding!!.radiostudent.isChecked) {
            ActivityUtils.launchActivity(this@LoginActivity, SignupStart::class.java)
        } else {
            ActivityUtils.launchActivity(this@LoginActivity, TutorSignupActivity::class.java)

        }
    }

    fun updatepasswordVisibility() {
        updatePasswordVisibility(mBinding!!.edtPassword)

    }


    companion object {
        const val STUDENT_FEATURE = "com.autohub.studentmodule.activities.StudentHomeActivity"
        const val TUTOR_FEATURE = "com.autohub.tutormodule.ui.dashboard.DashboardActivity"
    }
}
