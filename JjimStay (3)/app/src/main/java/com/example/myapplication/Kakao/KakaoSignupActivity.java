package com.example.myapplication.Kakao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MainLogin;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

/**
 * Created by KangJinho on 2016-09-22.
 */

public class KakaoSignupActivity extends Activity {

    public static String userName;
    public static String userProfile;
    public static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String mesaage = "failed to get user info. msg = " + errorResult;
                Logger.d(mesaage);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();

                } else {
                    redirectLoginActivity();
                }

            }

            @Override
            public void onSuccess(UserProfile result) {
                Logger.d("UserProfile : " + result);
                userName = result.getNickname();
                userProfile = result.getProfileImagePath();
                userId = String.valueOf(result.getId());
                Toast.makeText(getApplicationContext(), result.getNickname() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                redirectMainActivty();
            }
        });
    }

    private void redirectMainActivty() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, MainLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
