package com.christophelai.idp_colistraking;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SaveIdentityActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    private final int IMG_REQUEST = 1;
    Uri picUri;
    CheckBox cbIsIDCard;
    EditText imageNameText;
    String imageNameTextValue;
    Spinner spin;
    ArrayAdapter<String> adapter;
    String[] spinnerStatusList = {"Carte d'identité", "Colis endommagé", "Produit endommagé", "Colis et produit endommagés", "Inconnu a l'adresse", "Signature"};
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private String filePath;
    private Button UploadBn, ChooseBn;
    private EditText name;
    private ImageView imgView;
    private Bitmap bitmap;
    private String UploadUrl = Constant.SERVER + "/api-delivery/upload";
    private String nCommande;
    private String nomImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_identity);
        Intent saveIdentityIntent = getIntent();
        nCommande = saveIdentityIntent.getStringExtra("nCommande");
        spin = findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(SaveIdentityActivity.this, android.R.layout.simple_spinner_item, spinnerStatusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
        Button UploadBn = findViewById(R.id.fab);
        UploadBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });
        Button fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SaveIdentityActivity.this);
                builder1.setMessage("Toutes les informations sont correctes ? \n \n Fichier : " + nomImage);
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                uploadImage();
                                //Call saving id image function
                                saveIdentityImage();
                            }
                        });
                builder1.setNegativeButton(
                        "non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
                //Toast.makeText(getApplicationContext(), "Upload clicked", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSign = findViewById(R.id.btnSign);
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SaveIdentityActivity.this, SignActivity.class);
                i.putExtra("nCommande", nCommande);
                startActivity(i);
                finish();
            }
        });
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SaveIdentityActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void uploadImage() {
        Log.e("uploadImage ", " bitmap : " + bitmap);
        if (bitmap == null) {
            Log.e("uploadImage ", " condition : " + bitmap);
            Toast.makeText(SaveIdentityActivity.this, "Merci de sélectionner une image", Toast.LENGTH_LONG).show();
        } else if (bitmap != null) {
            Log.e("uploadImage ", " condition : " + bitmap);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Response", " args : " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String Response = jsonObject.getString("response");
                        Toast.makeText(SaveIdentityActivity.this, Response, Toast.LENGTH_LONG).show();
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageResource(R.drawable.upload);
                    } catch (JSONException e) {
                        Log.e("Response", " args : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Response", " args : " + error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", nCommande);
                    params.put("name", nomImage);
                    params.put("image", imageToString(bitmap));
                    return params;
                }
            };
            MySingleton.getInstance(SaveIdentityActivity.this).addToRequestQue(stringRequest);
        }

    }

    //Save identity photo
    private void saveIdentityImage() {
        Log.e("Nom id image:", nomImage);
        if (bitmap == null) {
            Log.e("uploadImage ", " condition : " + "null");
            Toast.makeText(SaveIdentityActivity.this, "Merci de choisir une photo", Toast.LENGTH_LONG).show();
        } else if (bitmap != null) {
            Log.e("uploadImage ", " condition : " + bitmap);
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, nomImage, "ID Description");
        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        Log.i("imageToString", " args : " + Base64.encodeToString(imgBytes, Base64.DEFAULT));
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {

            ImageView imageView = findViewById(R.id.imageView);

            if (requestCode == IMAGE_RESULT) {
                filePath = getImageFilePath(data);
                Log.i("getData", " args : " + filePath);
                if (filePath != null) {
                    //  bitmap = BitmapFactory.decodeFile(filePath);
                    bitmap = BitmapFactory.decodeFile(filePath);
                }
                imageView.setImageBitmap(bitmap);

            }

        }

    }


    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        imageNameTextValue = adapterView.getItemAtPosition(i).toString();
        nomImage = imageNameTextValue + "_" + nCommande + "_" + Constant.getToday("yyyy_MM_dd_HH_mm_ss");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        imageNameTextValue = "Carte d'identité";
        nomImage = imageNameTextValue + "_" + nCommande + "_" + Constant.getToday("yyyy_MM_dd_HH_mm_ss");
    }
}