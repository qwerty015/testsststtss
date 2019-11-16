package com.autohub.studentmodule.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.autohub.skln.BaseActivity
import com.autohub.skln.models.Request
import com.autohub.skln.models.RequestViewModel
import com.autohub.skln.models.User
import com.autohub.skln.models.tutormodels.UserViewModelold
import com.autohub.skln.utills.AppConstants
import com.autohub.skln.utills.GlideApp
import com.autohub.studentmodule.R
import com.autohub.studentmodule.databinding.ActivityTutorFullProfileBinding
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage

/**
 * Created by Vt Netzwelt
 */

class TutorFullProfileActivity : BaseActivity() {

    private var mBinding: ActivityTutorFullProfileBinding? = null
    private var mUserViewModel: UserViewModelold? = null
    private var mCurrentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tutor_full_profile)
        mBinding!!.callback = this


        getIntentData()

        mBinding!!.model = mUserViewModel
        setUpView()
        getCurrentUser()
        mBinding!!.bio.setShowingLine(2)
        mBinding!!.bio.addShowMoreText(getString(R.string.readmore))
        mBinding!!.bio.addShowLessText(getString(R.string.readless))
        mBinding!!.bio.text = mUserViewModel!!.bioData
        mBinding!!.bio.setShowLessTextColor(ContextCompat.getColor(this, R.color.readmorecolor))
        mBinding!!.bio.setShowMoreColor(ContextCompat.getColor(this, R.color.readmorecolor))

    }

    private fun getIntentData() {
        if (intent.extras != null) {
            val bundle = intent.extras
            val user = bundle!!.getParcelable<User>(AppConstants.KEY_DATA)
            mUserViewModel = UserViewModelold(user!!)
        }
    }


    private fun getCurrentUser() {
        firebaseStore.collection(getString(com.autohub.skln.R.string.db_root_students)).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener { documentSnapshot -> mCurrentUser = documentSnapshot.toObject(User::class.java) }
                .addOnFailureListener { e -> showSnackError(e.message) }
    }

    private fun setUpView() {
        addSubjectRadioButtons(mUserViewModel!!.user.subjectsToTeach.split(","))
        mUserViewModel!!.user.classType?.split(",")?.let { addClassTypeRadioButtons(it) }

        mBinding!!.txtdistence.text = mUserViewModel!!.user.distance.toString()


        if (mUserViewModel!!.user.pictureUrl != null) {
            val pathReference1 = FirebaseStorage.getInstance().reference.child(mUserViewModel!!.user.pictureUrl)
            GlideApp.with(this)
                    .load(pathReference1)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(com.autohub.skln.R.drawable.default_pic)
                    .fallback(com.autohub.skln.R.drawable.default_pic)
                    .into(mBinding!!.profilePicture)
        }

    }

    private fun makeRequest(subject: String, classtype: String) {
        showLoading()


        val studentId = firebaseAuth.currentUser!!.uid
        val tutorId = mUserViewModel!!.userId
        val request = Request(studentId, tutorId, subject, mUserViewModel!!.firstName, mCurrentUser!!.firstName, mCurrentUser!!.studentClass, classtype)///* mUserViewModel!!.classType*/
        val dbRoot = getString(R.string.db_root_requests)
        firebaseStore.collection(dbRoot).add(request).addOnSuccessListener { documentReference ->
            Log.d(">>>>Request", "DocumentSnapshot added with ID: " + documentReference.id)
            hideLoading()
            val intent = Intent()
            val bundle = Bundle()
            bundle.putParcelable(AppConstants.KEY_DATA, RequestViewModel(request, "Student", documentReference.id))
            intent.putExtra(AppConstants.KEY_DATA, bundle)
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }.addOnFailureListener { hideLoading() }
    }

    private fun addSubjectRadioButtons(subjects: List<String>) {
        if (subjects.isEmpty()) {
            val rdbtn = RadioButton(this)
            rdbtn.id = View.generateViewId()
            //rdbtn.text = "Radio " + rdbtn.id
            rdbtn.text = mUserViewModel!!.user.subjectsToTeach
            mBinding!!.classtyperadio.addView(rdbtn)
        }

        for (element in subjects) {
            val rdbtn = RadioButton(this)
            rdbtn.id = View.generateViewId()
            //rdbtn.text = "Radio " + rdbtn.id
            rdbtn.text = element
            mBinding!!.subjectradio.addView(rdbtn)
        }
    }

    private fun addClassTypeRadioButtons(classtype: List<String>) {
        if (classtype.isEmpty()) {
            val rdbtn = RadioButton(this)
            rdbtn.id = View.generateViewId()
            //rdbtn.text = "Radio " + rdbtn.id
            rdbtn.text = mUserViewModel!!.user.classType
            mBinding!!.classtyperadio.addView(rdbtn)
        }

        for (element in classtype) {
            val rdbtn = RadioButton(this)
            rdbtn.id = View.generateViewId()
            //rdbtn.text = "Radio " + rdbtn.id
            rdbtn.text = element
            mBinding!!.classtyperadio.addView(rdbtn)
        }
    }

    fun onBackClick() {
        finish()
    }

    fun onRequestClick() {

        when {
            mBinding!!.subjectradio.checkedRadioButtonId == -1 -> showSnackError("Please select a subject")
            mBinding!!.classtyperadio.checkedRadioButtonId == -1 -> showSnackError("Please select a class type")
            else -> {
                var selectedsubjectRadio = (findViewById(mBinding!!.subjectradio.checkedRadioButtonId)) as RadioButton
                println("==================== Selected subject" + selectedsubjectRadio.text.toString())

                var selectedclasstypeRadio = (findViewById(mBinding!!.classtyperadio.checkedRadioButtonId)) as RadioButton
                println("==================== Selected subject" + selectedclasstypeRadio.text.toString())
                makeRequest(selectedsubjectRadio.text.toString(), selectedclasstypeRadio.text.toString())


            }
        }
    }
}