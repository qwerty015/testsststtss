package com.autohub.skln.activities.user

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.autohub.skln.BaseActivity
import com.autohub.skln.R
import com.autohub.skln.databinding.TutorSignupStartBinding
import com.autohub.skln.utills.ActivityUtils
import com.autohub.skln.utills.AppConstants.*
import com.autohub.skln.utills.GpsUtils
import com.autohub.skln.utills.LocationProvider
import com.google.android.gms.location.LocationListener
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class SignupStart : BaseActivity() {
    private var mBinding: TutorSignupStartBinding? = null
    private var mCity: String? = null
    private var mLocation: Location? = null
    private val mLocationListener = LocationListener { location ->
        Log.d(">>>>Location", location.toString())
        mLocation = location
        LocationProvider.getInstance().getAddressFromLocation(this@SignupStart, location) { address ->
            Log.d(">>>>LocationAddress", "Address is :$address")
            mCity = address
        }
    }

    private fun getEncryptedPassword(): String {
        try {
            return BaseActivity.encrypt(getString(mBinding!!.edtPassword.text))
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }

        return getString(mBinding!!.edtPassword.text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.tutor_signup_start)
        mBinding!!.callback = this

        val mGpsUtils = GpsUtils(this)

        if (!mGpsUtils.isGpsEnabled) {
            mGpsUtils.turnGPSOn { Log.d(">>>>Location", "enabled") }
        }
    }

    fun onNextClick() {
        if (isValid(mBinding!!.edtFirstName, mBinding!!.edtLastName)) {
            val password = mBinding!!.edtPassword.text
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

            if (password == null || password.length == 0) {
                mBinding!!.edtPassword.error = resources.getString(R.string.enter_password)
                mBinding!!.edtPassword.requestFocus()
//                showSnackError(R.string.enter_password)
                return
            }

            if (mLocation == null || TextUtils.isEmpty(mCity)) {
                Toast.makeText(this, "Please check you GPS setting, we need You location", Toast.LENGTH_SHORT).show()
                return
            }
            makeSaveRequest()
        }
    }

    private fun makeSaveRequest() {

        var userMap = HashMap<String, Any>()
        userMap.put(KEY_FIRST_NAME, mBinding!!.edtFirstName.text.toString())
        userMap.put(KEY_LAST_NAME, mBinding!!.edtLastName.text.toString())
        userMap.put(KEY_EMAIL, mBinding!!.edtemail.text.toString())
        userMap.put(KEY_SEX, if (mBinding!!.radioMale.isChecked) MALE else FEMALE)
        userMap.put(KEY_PASSWORD, getEncryptedPassword())
        userMap.put(KEY_CITY, mCity!!)
        userMap.put(KEY_LATITUDE, mLocation!!.latitude)
        userMap.put(KEY_LONGITUDE, mLocation!!.longitude)
        var extras = Bundle()
        extras.putSerializable(KEY_USERMAP, userMap)

        ActivityUtils.launchActivity(this@SignupStart, NumberVerificationActivity::class.java, extras)

        //  showLoading();
        /*  final Map<String, Object> user = new HashMap<>();
        user.put(KEY_FIRST_NAME, mBinding.edtFirstName.getText().toString());
        user.put(KEY_LAST_NAME, mBinding.edtLastName.getText().toString());
        user.put(KEY_SEX, mBinding.radioMale.isChecked() ? MALE : FEMALE);
        user.put(KEY_PASSWORD, getEncryptedPassword());
//        user.put(KEY_PHONE_NUMBER, phoneNumber);
        user.put(KEY_CITY, mCity);
        user.put(KEY_LATITUDE, mLocation.getLatitude());
        user.put(KEY_LONGITUDE, mLocation.getLongitude());
        String dbRoot = getString(R.string.db_root_students);
        if (TYPE_TUTOR.equalsIgnoreCase(mType)) {
            dbRoot = getString(R.string.db_root_tutors);
        }*/

        /* getFirebaseStore().collection(dbRoot).document(getFirebaseAuth().getCurrentUser().getUid()).set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoading();
                        ActivityUtils.launchActivity(SignupStart.this,
                                mType.equalsIgnoreCase(TYPE_TUTOR) ?
                                        TutorCategorySelect.class : StudentClassSelect.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoading();
                        showSnackError(e.getMessage());
                    }
                });*/


        /* ActivityUtils.launchActivity(SignupStart.this,
                                mType.equalsIgnoreCase(TYPE_TUTOR) ?
                                        TutorCategorySelect.class : StudentClassSelect.class);*/
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onResume() {
        super.onResume()
        if (checkGooglePlayServices() && isLocationPermissionGranted) {
            Log.d(">>>>Location", "Oncreate")
            LocationProvider.getInstance().start(this, mLocationListener)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 2321)
        }
    }

    override fun onPause() {
        super.onPause()
        LocationProvider.getInstance().stopLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2321) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkGooglePlayServices() && isLocationPermissionGranted) {
                    LocationProvider.getInstance().start(this, mLocationListener)
                }
            }
        }
    }
}