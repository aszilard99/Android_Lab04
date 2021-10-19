package com.example.android_lab04

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    lateinit var toast : Toast
    lateinit var nextButton : Button
    lateinit var nameEditText: EditText
    lateinit var contactsButton: Button

    //contact permission code
    private val CONTACT_PERMISSION_CODE = 1;
    //contact pick code
    private val CONTACT_PICK_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toast = Toast.makeText(this,"Activity Created", Toast.LENGTH_SHORT)
        toast.show()
        nextButton = findViewById(R.id.nextButton)
        nextButton.setOnClickListener { startGreetingActivity() }
        nameEditText = findViewById(R.id.editTextName)
        contactsButton = findViewById(R.id.contactsButton)

        contactsButton.setOnClickListener {
            //check permission allowed or not
            if (checkContactPermission()){
                //allowed
                pickContact()
            }
            else{
                //not allowed, request
                requestContactPermission()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        toast = Toast.makeText(this,"Activity Started", Toast.LENGTH_SHORT)
        toast.show()
    }
    override fun onResume() {
        super.onResume()
        toast = Toast.makeText(this,"Activity Resumed", Toast.LENGTH_SHORT)
        toast.show()
    }
    override fun onRestart() {
        super.onRestart()
        toast = Toast.makeText(this,"Activity Restarted", Toast.LENGTH_SHORT)
        toast.show()
    }
    override fun onPause() {
        super.onPause()
        toast = Toast.makeText(this,"Activity paused", Toast.LENGTH_SHORT)
        toast.show()
    }
    override fun onStop() {
        super.onStop()
        toast = Toast.makeText(this,"Activity stopped", Toast.LENGTH_SHORT)
        toast.show()
    }
    override fun onDestroy() {
        super.onDestroy()
        toast = Toast.makeText(this,"Activity destroyed", Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun startGreetingActivity(){
        val intent = Intent(this, GreetingActivity::class.java).apply {
            val tmp = nameEditText.getText().toString()
            putExtra("NAME", tmp)
        }
        startActivity(intent)
    }

    private fun checkContactPermission(): Boolean{
        //check if permission was granted/allowed or not, returns true if granted/allowed, false if not
        return  ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactPermission(){
        //request the READ_CONTACTS permission
        val permission = arrayOf(android.Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE)
    }

    private fun pickContact(){
        //intent ti pick contact
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //handle permission request results || calls when user from Permission request dialog presses Allow or Deny
        if (requestCode == CONTACT_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted, can pick contact
                pickContact()
            }
            else{
                //permission denied, cann't pick contact, just show message
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //handle intent results || calls when user from Intent (Contact Pick) picks or cancels pick contact
        if (resultCode == RESULT_OK){
            //calls when user click a contact from contacts (intent) list
            if (requestCode == CONTACT_PICK_CODE){


                val cursor1: Cursor
                val cursor2: Cursor?

                //get data from intent
                val uri = data!!.data
                cursor1 = contentResolver.query(uri!!, null, null, null, null)!!
                if (cursor1.moveToFirst()){
                    //get contact details
                    val contactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                    val contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val contactThumbnail = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))
                    val idResults = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    val idResultHold = idResults.toInt()
                    //set details: contact id, contact name, image
                    /*binding.contactTv.append("ID: $contactId")
                    binding.contactTv.append("\nName: $contactName")*/
                    /*//set image, first check if uri/thumbnail is not null
                    if (contactThumbnail != null){
                        binding.thumbnailIv.setImageURI(Uri.parse(contactThumbnail))
                    }
                    else{
                        binding.thumbnailIv.setImageResource(R.drawable.ic_person)
                    }*/

                    //check if contact has a phone number or not
                    if (idResultHold == 1){
                        cursor2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+contactId,
                            null,
                            null
                        )
                        //a contact may have multiple phone numbers
                        while (cursor2!!.moveToNext()){
                            //get phone number
                            val contactNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            //set phone number
                            nameEditText.setText(contactNumber)

                        }
                        cursor2.close()
                    }
                    cursor1.close()
                }
            }

        }
        else{
            //cancelled picking contact
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}