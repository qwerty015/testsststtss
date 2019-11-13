package com.netzwelt.loginsignup.student

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.autohub.skln.BaseActivity
import com.google.firebase.firestore.SetOptions

import java.util.ArrayList
import java.util.HashMap

import com.netzwelt.loginsignup.student.fragments.StudentSubjectSelectSeniorFragment
import com.netzwelt.loginsignup.listners.ClassSelectionListner
import com.netzwelt.loginsignup.student.models.SubjectsData

import com.autohub.skln.utills.AppConstants.KEY_STDT_FAVORITE_CLASSES
import com.autohub.skln.utills.AppConstants.KEY_STDT_LEAST_FAV_CLASSES
import com.autohub.skln.utills.AppConstants.SUBJECT_ACCOUNTANCY
import com.autohub.skln.utills.AppConstants.SUBJECT_BIOLOGY
import com.autohub.skln.utills.AppConstants.SUBJECT_BUSINESS
import com.autohub.skln.utills.AppConstants.SUBJECT_CHEMISTRY
import com.autohub.skln.utills.AppConstants.SUBJECT_COMPUTER_SCIENCE
import com.autohub.skln.utills.AppConstants.SUBJECT_ECONOMICS
import com.autohub.skln.utills.AppConstants.SUBJECT_ENGLISH
import com.autohub.skln.utills.AppConstants.SUBJECT_MATHS
import com.autohub.skln.utills.AppConstants.SUBJECT_PHYSICS
import com.netzwelt.loginsignup.R
import com.netzwelt.loginsignup.databinding.ActivityStudentSubjectSeniorBinding
import com.netzwelt.loginsignup.utility.Utilities

/**
 * Created by Vt Netzwelt
 */
class StudentSubjectSelectSeniorActivity : BaseActivity(), ClassSelectionListner {

    private var selectedSubjects: ArrayList<String> = ArrayList()

    override fun selectedClass(position: Int, isSecondSelected: Boolean, selectedClass: String) {


        for (i in fragmentsList.indices) {
            if (position == i) {
                if (isSecondSelected) {
                    if (selectedSubjects.contains(selectedClass)) {
                        selectedSubjects.remove(selectedClass)
                        fragmentsList[i].updateFragment(isSecond = true, selected = false)

                    } else {
                        selectedSubjects.add(selectedClass)

                        fragmentsList[i].updateFragment(isSecond = true, selected = true)

                    }

                } else {
                    if (selectedSubjects.contains(selectedClass)) {
                        selectedSubjects.remove(selectedClass)
                        fragmentsList[i].updateFragment(isSecond = false, selected = false)

                    } else {
                        selectedSubjects.add(selectedClass)

                        fragmentsList[i].updateFragment(isSecond = false, selected = true)

                    }
                }


            }
        }
    }


    private var mFavoriteOrLeast: Boolean = false

    private var mBinding: ActivityStudentSubjectSeniorBinding? = null
    private var subjectsDataList: ArrayList<SubjectsData> = ArrayList()
    private var fragmentsList: ArrayList<StudentSubjectSelectSeniorFragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_student_subject_senior)
        mBinding!!.callback = this
        selectedSubjects = ArrayList()
        insertSubjectData()
        val countList: ArrayList<String> = ArrayList()
        countList.add("1")
        countList.add("2")
        countList.add("3")
        countList.add("4")
        countList.add("5")


        mFavoriteOrLeast = intent.getBooleanExtra("favorite_or_least", true)

        if (mFavoriteOrLeast) {
            val spannable = SpannableStringBuilder(resources.getString(R.string.select_favorite_subject))
            spannable.setSpan(ForegroundColorSpan(Color.BLUE), 12, 21, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            spannable.setSpan(UnderlineSpan(), 12, 21, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            this.mBinding!!.tvSelectText!!.setText(spannable, TextView.BufferType.SPANNABLE)
            Utilities.animateProgressbar(mBinding!!.pbSignupProgress, 40.0f, 60.0f)

        } else {
            Utilities.animateProgressbar(mBinding!!.pbSignupProgress, 60.0f, 80.0f)

            val spannable = SpannableStringBuilder(resources.getString(R.string.select_least_favorite_subject))
            spannable.setSpan(ForegroundColorSpan(Color.RED), 12, 27, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            spannable.setSpan(UnderlineSpan(), 12, 27, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            mBinding!!.tvSelectText!!.setText(spannable, TextView.BufferType.SPANNABLE)
        }

        getFragments(countList)
        val pagerAdapter = PagerAdapter(supportFragmentManager, fragmentsList)
        mBinding!!.viewpager.adapter = pagerAdapter
        mBinding!!.viewpager.offscreenPageLimit = 4
        mBinding!!.wormDotsIndicator.setViewPager(mBinding!!.viewpager)
    }

    private fun getFragments(countList: ArrayList<String>) {


        for (position in countList.indices) {

            when (position) {
                0 -> fragmentsList.add(StudentSubjectSelectSeniorFragment.newInstance(position, ArrayList(subjectsDataList.subList(0, 2))))
                1 -> fragmentsList.add(StudentSubjectSelectSeniorFragment.newInstance(position, ArrayList(subjectsDataList.subList(2, 4))))
                2 -> fragmentsList.add(StudentSubjectSelectSeniorFragment.newInstance(position, ArrayList(subjectsDataList.subList(4, 6))))
                3 -> fragmentsList.add(StudentSubjectSelectSeniorFragment.newInstance(position, ArrayList(subjectsDataList.subList(6, 8))))
                else -> fragmentsList.add(StudentSubjectSelectSeniorFragment.newInstance(position, ArrayList(subjectsDataList.subList(8, 9))))
            }
        }
    }

    private fun insertSubjectData() {
        subjectsDataList.add(SubjectsData(R.color.math, com.autohub.skln.R.drawable.geometry, false, SUBJECT_MATHS))
        subjectsDataList.add(SubjectsData(R.color.english, com.autohub.skln.R.drawable.noun, false, SUBJECT_ENGLISH))
        subjectsDataList.add(SubjectsData(R.color.physics, com.autohub.skln.R.drawable.physics, false, SUBJECT_PHYSICS))
        subjectsDataList.add(SubjectsData(R.color.account, com.autohub.skln.R.drawable.accounting, false, SUBJECT_ACCOUNTANCY))
        subjectsDataList.add(SubjectsData(R.color.biology, com.autohub.skln.R.drawable.microscope, false, SUBJECT_BIOLOGY))
        subjectsDataList.add(SubjectsData(R.color.computerscience, com.autohub.skln.R.drawable.informatic, false, SUBJECT_COMPUTER_SCIENCE))
        subjectsDataList.add(SubjectsData(R.color.chemistry, com.autohub.skln.R.drawable.test_tube, false, SUBJECT_CHEMISTRY))
        subjectsDataList.add(SubjectsData(R.color.economics, com.autohub.skln.R.drawable.rating, false, SUBJECT_ECONOMICS))
        subjectsDataList.add(SubjectsData(R.color.business, com.autohub.skln.R.drawable.rupee, false, SUBJECT_BUSINESS))


    }

    fun onNextClick() {
        val stringBuilder = StringBuilder()
        if (selectedSubjects.size > 0) {
            stringBuilder.append(selectedSubjects[0])
            for (i in 1 until selectedSubjects.size) {
                stringBuilder.append(", ").append(selectedSubjects[i])
            }
        }

        if (stringBuilder.isEmpty()) {
            showSnackError(R.string.choose_subjects)
            return
        }

        showLoading()

        val user = HashMap<String, Any>()
        if (mFavoriteOrLeast)
            user[KEY_STDT_FAVORITE_CLASSES] = stringBuilder.toString()
        else
            user[KEY_STDT_LEAST_FAV_CLASSES] = stringBuilder.toString()

        firebaseStore.collection(getString(R.string.db_root_students)).document(firebaseAuth.currentUser!!.uid).set(user, SetOptions.merge())
                .addOnSuccessListener {
                    hideLoading()
                    val i: Intent
                    if (mFavoriteOrLeast) {
                        i = Intent(this@StudentSubjectSelectSeniorActivity, StudentSubjectSelectSeniorActivity::class.java)
                        i.putExtra("favorite_or_least", false)
                    } else {
                        i = Intent(this@StudentSubjectSelectSeniorActivity, StudentHobbySelect::class.java)
                    }
                    startActivity(i)
                }
                .addOnFailureListener { e ->
                    hideLoading()
                    showSnackError(e.message)
                }
    }


    inner class PagerAdapter(fragmentManager: FragmentManager, private var fragmentsList: ArrayList<StudentSubjectSelectSeniorFragment>) :
            FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        // 2
        override fun getItem(position: Int): Fragment {
            return fragmentsList[position]
        }

        override fun getPageWidth(position: Int): Float {
            return 0.5f
        }

        // 3
        override fun getCount(): Int {
            return fragmentsList.size
        }
    }

    /* override fun attachBaseContext(newBase: Context) {
         super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
     }*/


}
