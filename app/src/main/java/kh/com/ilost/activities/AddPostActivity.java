package kh.com.ilost.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import kh.com.ilost.R;
import kh.com.ilost.helpers.Utility;
import kh.com.ilost.models.Post;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReferencePost;
    private StorageReference storageReference;
    private ImageView imgAddImage;
    private EditText edtTitle, edtDate, edtTimeStart, edtTimeEnd;
    private Spinner spnCat, spnLocation;
    private RadioGroup rdoGroupType;
    private ProgressDialog progressDialog;

    private String currentPhotoPath = "";

    private static Uri photoPath;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int REQUEST_CHOOSE_IMAGE = 1002;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        setTitle("Add New Post");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RadioButton rdoLost = findViewById(R.id.post_rdo_lost);
        Button btnSave = findViewById(R.id.post_btn_save);
        imgAddImage = findViewById(R.id.post_img_btn_add_image);
        edtTitle = findViewById(R.id.post_edt_title);
        edtDate = findViewById(R.id.post_txt_date);
        edtTimeStart = findViewById(R.id.post_txt_start_time);
        edtTimeEnd = findViewById(R.id.post_txt_end_time);
        spnCat = findViewById(R.id.post_spn_categories);
        spnLocation = findViewById(R.id.post_sp_location);
        rdoGroupType = findViewById(R.id.post_rdo_group_type);

        databaseReferencePost = FirebaseDatabase.getInstance().getReference("posts");
        storageReference = FirebaseStorage.getInstance().getReference();

        rdoLost.setChecked(true);
        btnSave.setOnClickListener(this);
        imgAddImage.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.post_img_btn_add_image) {
            popUpDialog();
        } else if (id == R.id.post_btn_save) {
            addNewPost();
        }
    }

    private void addNewPost() {
        progressDialog.setMessage("Posting...");
        progressDialog.show();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (!validForm()) {
                return;
            }
            String key = databaseReferencePost.push().getKey();
            uploadPostImage(key);
            String title = edtTitle.getText().toString().trim();
            int checkedRadioButtonId = rdoGroupType.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(checkedRadioButtonId);
            String type = radioButton.getText().toString();
            String date = edtDate.getText().toString();
            String timeStart = edtTimeStart.getText().toString().trim();
            String timeEnd = edtTimeEnd.getText().toString().trim();
            String categories = spnCat.getSelectedItem().toString().trim();
            String location = spnLocation.getSelectedItem().toString().toLowerCase().trim();
            Post post = new Post(key, title, type, System.currentTimeMillis(), date, firebaseUser.getUid(),
                    timeStart, timeEnd, categories, location);
            databaseReferencePost.child(key).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.hide();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Authentication Required",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void popUpDialog() {
        final String[] options = {"Take Photo", "Choose From Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                boolean permissionGranted = Utility.checkPermission(AddPostActivity.this);
                if (options[item].equals("Take Photo")) {
                    if (permissionGranted) {
                        dispatchTakePictureIntent();
                    }
                } else if (options[item].contentEquals("Choose From Gallery")) {
                    if (permissionGranted) {
                        galleryIntent();
                    }
                }
            }
        });
        builder.show();

//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        photoPath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "fname_" +
//                String.valueOf(System.currentTimeMillis()) + ".jpg"));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
//        startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("app", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                photoPath = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Image"), REQUEST_CHOOSE_IMAGE);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.ENGLISH).format(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,          /* prefix */
                ".jpg",         /* suffix */
                storageDir              /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                onCaptureImageResult();
            } else if (requestCode == REQUEST_CHOOSE_IMAGE) {
                onSelectFromGalleryResult(data);
            }
        }
    }

    private void onCaptureImageResult() {
        Log.d("app", photoPath.toString() + "----" + currentPhotoPath);
        // Get the dimensions of the View
        int targetW = imgAddImage.getWidth();
        int targetH = imgAddImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imgAddImage.setImageBitmap(bitmap);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                photoPath = data.getData();
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),
                        data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgAddImage.setImageBitmap(bm);
    }

    private void uploadPostImage(final String key) {
        if (photoPath != null) {
            final StorageReference ref = storageReference.child("post_images/" + UUID.randomUUID().toString());
            ref.putFile(photoPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            product.setPurl(taskSnapshot.getUploadSessionUri() + "");
//                            databaseReferencePost.child(key).child("purl").setValue(product.getPurl());
//                            Log.d("app1", photoPath.toString());
                            photoPath = taskSnapshot.getUploadSessionUri();
//                            Log.d("app2", photoPath.toString());
                            progressDialog.hide();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.hide();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        }
//        return photoPath.toString();
    }


    private boolean validForm() {
        boolean valid = false;
        String title = edtTitle.getText().toString().trim();
        if (title.isEmpty()) {
            edtTitle.setError("Required");
        } else {
            edtTitle.setError(null);
            valid = true;
        }
        return valid;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
