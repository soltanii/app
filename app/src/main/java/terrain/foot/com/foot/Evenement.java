package terrain.foot.com.foot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;



public  class Evenement extends AppCompatActivity {
    ImageButton  back;
    ImageView refresh,addpub;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ListView publications;
    String id,Email;
    Query query;
    Button button;
    ViewGroup footer=null;
ValueEventListener value;
    Dialog myDialog1;
    int nombre=7;
    ArrayList<Publication> pub= new ArrayList<>();
    ArrayAdapter<Publication> adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evenement);
        View decorView = this.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
     //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  ifasakh el bare el fou9ani (status bar )
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        Email=intent.getStringExtra("Email");

        refresh=(ImageView)findViewById(R.id.refresh0);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh.setColorFilter(R.color.colorPrimaryDark);
                Intent intent = new Intent(Evenement.this,Evenement.class);
                intent.putExtra("id", id);
                intent.putExtra("Email",Email);
                myRef.child(id).child("publication").removeEventListener(value);
                startActivity(intent);
                finish();
            }
        });


        back=findViewById(R.id.back);

        publications = findViewById(R.id.pub);
        adapter = new PublicationAdapter(this, pub);

        database = FirebaseDatabase.getInstance();
        myRef=database.getReference("users");
        if(pub.isEmpty()) {
            remplipub(7);
        }
        else {
            publications.setAdapter(adapter);
        }

        if(pub.size()>=7&& footer==null){
            footer = (ViewGroup) getLayoutInflater().inflate(R.layout.footerfeedback,publications,false);
           publications.addFooterView(footer,null,true);
            button=(Button)findViewById(R.id.ajouter);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nombre=nombre+7;
                    remplipub(nombre);
                    adapter.notifyDataSetChanged();
                 }
            });}
        if(pub.size()<7 && footer!=null) {publications.removeFooterView(footer);}


        publications.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id1) {
                AlertDialog.Builder builder =new AlertDialog.Builder(Evenement.this);
                builder.setMessage("voulez vous suprimer cette publication").setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myRef.child(id).child("publication").child(pub.get(position).getKey()).removeValue();
                       pub.remove(position);
                        adapter.notifyDataSetChanged();

                    }
                }).setNegativeButton("Non",null);
                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                return false;
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                back.setColorFilter(R.color.colorPrimaryDark);
                Intent intent = new Intent(Evenement.this,Profile.class);
                intent.putExtra("id", id);
                intent.putExtra("Email",Email);
                myRef.child(id).child("publication").removeEventListener(value);
                startActivity(intent);
                finish();
            }
        });

        addpub=(ImageView)findViewById(R.id.addpub);
        addpub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addpub.setColorFilter(R.color.colorPrimaryDark);
                Intent intent = new Intent(Evenement.this,Tournoi.class);
                intent.putExtra("id", id);
                intent.putExtra("Email",Email);
                myRef.child(id).child("publication").removeEventListener(value);
                query.removeEventListener(value);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }
public void remplipub(int nbr){
    query = myRef.child(id).child("publication").orderByKey()
            .limitToLast(nbr);
   value= new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            pub.clear();
            if(dataSnapshot.exists()) {

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.hasChild("text") && data.hasChild("img")) {
                        pub.add(new Publication(data.child("text").getValue().toString(), data.child("img").getValue().toString(), data.child("titre").getValue().toString(),data.getKey()));
                    }else if(!data.hasChild("text")){
                        pub.add(new Publication("", data.child("img").getValue().toString(), data.child("titre").getValue().toString(),data.getKey()));

                    }else if(!data.hasChild("img")){
                        pub.add(new Publication(data.child("text").getValue().toString(),"",  data.child("titre").getValue().toString(),data.getKey()));

                    }

                }

                Collections.reverse(pub);
            }

            if(pub.size()>=7 && footer==null){
                footer = (ViewGroup) getLayoutInflater().inflate(R.layout.footerfeedback,publications,false);
               publications.addFooterView(footer,null,true);

                button=(Button)findViewById(R.id.ajouter);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nombre=nombre+7;
                        remplipub(nombre);
                        adapter.notifyDataSetChanged();
                    }
                });}
            if(pub.size()<nombre && footer!=null) {publications.removeFooterView(footer);
                footer=null;
            }

            publications.setAdapter(adapter);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    query.addListenerForSingleValueEvent(value);
}
}
