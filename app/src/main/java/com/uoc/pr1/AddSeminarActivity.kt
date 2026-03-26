package com.uoc.pr1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.uoc.pr1.databinding.ActivityAddSeminarBinding

class AddSeminarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSeminarBinding
    private var uri: Uri? = null

    //BEGIN-CODE-UOC-6.3
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { selectedUri: Uri? ->
        uri = selectedUri
    }
    //END-CODE-UOC-6.3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSeminarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BEGIN-CODE-UOC-6.3
        binding.btnSelectImage.setOnClickListener {
            // Launch the gallery intent to pick an image
            selectImageLauncher.launch("image/*")
        }
        //END-CODE-UOC-6.3

        //BEGIN-CODE-UOC-6.4
        binding.btnNew.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val result = AddSeminarResult(title, uri)

            val resultIntent = Intent()
            resultIntent.putExtra(PARAM_ADDREQUESTRESULT_CLASS, result)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        //END-CODE-UOC-6.4
    }
}