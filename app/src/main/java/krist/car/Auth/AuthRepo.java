package krist.car.Auth;

import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;
import krist.car.Api.ApiSingleton;
import krist.car.Models.LoginFormModel;
import krist.car.Models.UploadUsersImage;
import krist.car.Models.UserModel;

public class AuthRepo {
    private ApiSingleton api;

    public AuthRepo() {
        api = ApiSingleton.getInstance();
    }

    public MutableLiveData<Boolean> firebaseSignInWithEmailAndPassword(LoginFormModel loginModel) {
        final MutableLiveData<Boolean> isLoginSuccess = new MutableLiveData<>();
        api.firebaseAuth.signInWithEmailAndPassword(loginModel.getName(), loginModel.getPassword())
                .addOnCompleteListener(task -> isLoginSuccess.setValue(task.isSuccessful()));
        return isLoginSuccess;
    }

    public MutableLiveData<Boolean> createUserWithEmailAndPassword(LoginFormModel loginModel) {
        final MutableLiveData<Boolean> isUserCreatedSuccessfully = new MutableLiveData<>();
        api.firebaseAuth.createUserWithEmailAndPassword(loginModel.getName(), loginModel.getPassword())
                .addOnCompleteListener(task -> {
                    isUserCreatedSuccessfully.setValue(task.isSuccessful());
                });
        return isUserCreatedSuccessfully;
    }

    public MutableLiveData<Boolean> registerUserData(UserModel userModel) {
        final MutableLiveData<Boolean> isUserRegisterSuccessfully = new MutableLiveData<>();

        String userId = api.getUserUId();
        userModel.setId(userId);

        api.getDatebaseReferenceToThisEndPoint("users").child(userId).setValue(userModel).addOnCompleteListener(task -> {
            isUserRegisterSuccessfully.setValue(task.isComplete());
        });

        return isUserRegisterSuccessfully;
    }

    public MutableLiveData<String> uploadPhoto(Uri imgFilePath, String fileExtension) {
        final MutableLiveData<String> isSuccess = new MutableLiveData<>();
        StorageReference reference = api.getFirebaseStorageToThisEndPoint("uploads").child(System.currentTimeMillis() + "." + fileExtension);

        reference.putFile(imgFilePath).continueWithTask( task -> {
            if(!task.isComplete()) {
                throw Objects.requireNonNull(task.getException());
            }else {
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener( task -> {
            String imageUri = task.getResult().toString();
            //Todo fix this model class
            isSuccess.setValue(imageUri);
        });

        return isSuccess;
    }
}
