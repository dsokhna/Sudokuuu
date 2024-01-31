package com.example.sudoku.view

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sudoku.R
import com.example.sudoku.viewModel.MyAdapter
import com.example.sudoku.viewModel.User
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class ProfileActivity: AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val  REQUEST_IMAGE_PICK =1
    }
    private lateinit var nom: TextView
    private lateinit var nomdutilisateur: TextView
    private lateinit var score: TextView
    private lateinit var profileImageView: ImageView
    var recview: RecyclerView? = null
    var adapter: MyAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        nom = findViewById(R.id.nom)
        nomdutilisateur = findViewById(R.id.nomdutilisateur)
        score = findViewById(R.id.score)
        profileImageView = findViewById<ImageView>(R.id.profileImageView)

        val shareButton = findViewById<Button>(R.id.partage)

        findViewById<Button>(R.id.Retour).setOnClickListener{finish()}

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            val userId = currentUser!!.uid


            // Read from the database
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val Nom = dataSnapshot.child("nom").getValue(String::class.java)

                    val Prenom = dataSnapshot.child("prenom").getValue(String::class.java)
                    val Score = dataSnapshot.child("score").getValue(Int::class.java)
                    val Nomdutilisateur =
                        dataSnapshot.child("nomdutilisateur").getValue(String::class.java)
                        nom.text = Nom+" "+Prenom
                    nomdutilisateur.text=nom.text
                    score.text = Score.toString()
                    val storageRef = FirebaseStorage.getInstance().getReference().child("users").child(userId)
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(this@ProfileActivity)
                            .load(uri)
                            .into(profileImageView)
                    }.addOnFailureListener {
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }

        profileImageView.setOnClickListener {
            val popupMenu = PopupMenu(this, profileImageView)

            // Charger le menu contextuel à partir du fichier XML
            popupMenu.inflate(R.menu.menu_photo)
            popupMenu.gravity =  Gravity.BOTTOM

            // Ajouter un écouteur de clic pour chaque élément de menu
            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {
                    R.id.camera -> {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                        true
                    }
                    R.id.profileImageView -> {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        startActivityForResult(intent, REQUEST_IMAGE_PICK)
                        true
                    }
                    R.id.supprimer ->{
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            val userId = currentUser.uid
                            val storageRef = FirebaseStorage.getInstance().reference.child("users").child(userId)
                            storageRef.delete().addOnSuccessListener {
                                // Photo deleted successfully
                             //   profileImageView.setImageResource(R.drawable.profile)
                            }.addOnFailureListener {
                                // Error while deleting photo
                            }
                        }
                        true

                    }
                    else -> {
                        false
                    }
                }
            }

            // Afficher le menu contextuel
            popupMenu.show()
        }

        shareButton.setOnClickListener {

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Qui fait Mieux que moi  ${score.text} !")
            startActivity(Intent.createChooser(shareIntent, "Partager mon score via :").apply {

                putExtra(
                    Intent.EXTRA_EXCLUDE_COMPONENTS, arrayOf(
                        ComponentName(
                            "com.android.bluetooth",
                            "com.android.bluetooth.opp.BluetoothOppLauncherActivity"
                        ),
                        ComponentName(
                            "com.google.android.apps.docs",
                            "com.google.android.apps.docs.app.SendTextToClipboardActivity"
                        )
                    )
                )
            })
        }
        recview = findViewById(R.id.recview)
        recview?.setLayoutManager(LinearLayoutManager(this))

        val options: FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()

            .setQuery(FirebaseDatabase.getInstance().reference.child("Users"), User::class.java)
            .build()

        adapter = MyAdapter(options)
        (recview as? RecyclerView)?.setAdapter(adapter)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Afficher l'image capturée dans imageView
            val imageBitmap = data?.extras?.get("data") as Bitmap
            profileImageView.setImageBitmap(imageBitmap)
                val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("users").child(userId)
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                storageRef.putBytes(data).addOnSuccessListener {
                    // Image upload successful, handle the result as necessary
                }.addOnFailureListener {
                    // Image upload failed, handle the error as necessary
                }
            }

        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            // Afficher l'image sélectionnée dans imageView
            val selectedImage = data?.data
            selectedImage?.let {
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                profileImageView.setImageBitmap(imageBitmap)
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userId = currentUser.uid
                    val storageRef = FirebaseStorage.getInstance().reference.child("users").child(userId)
                    val baos = ByteArrayOutputStream()
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    storageRef.putBytes(data).addOnSuccessListener {
                        // Image upload successful, handle the result as necessary
                    }.addOnFailureListener {
                        // Image upload failed, handle the error as necessary
                    }
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.searchmenu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = "Rechercher un utilisateur"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter!!.stopListening()
                if (newText != null && newText.isNotBlank()) {
                    val searchQuery = newText.trim()
                    val query = FirebaseDatabase.getInstance().reference.child("Users")
                        .orderByChild("nomdutilisateur").startAt(searchQuery).endAt(searchQuery + "\uf8ff")
                    val options: FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User::class.java)
                        .build()
                    adapter = MyAdapter(options)
                    recview!!.adapter = adapter
                    adapter!!.startListening()
                } else {
                    adapter!!.stopListening()
                    val options: FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().reference.child("Users"), User::class.java)
                        .build()
                    adapter = MyAdapter(options)
                    recview!!.adapter = adapter
                    adapter!!.startListening()
                }
                return true
            }
        })

        val options: FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(FirebaseDatabase.getInstance().reference.child("Users").orderByChild("nomdutilisateur").equalTo(""), User::class.java)
            .build()

        adapter = MyAdapter(options)
        recview!!.adapter = adapter
        adapter!!.startListening()

        return true
    }
    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    private fun processSearch(s: String) {
        val query = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("nom").startAt(s).endAt(s + "\uf8ff")
        val options: FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()
        adapter = MyAdapter(options)
        adapter!!.startListening()
        recview!!.adapter = adapter
    }
}