package com.example.rutamoviltransporte;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView oneCard, twoCard, treeCard, fourCard, fiveCard;
    private static long presione;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oneCard = findViewById(R.id.oneCard);
        twoCard = findViewById(R.id.twoCard);
        treeCard = findViewById(R.id.treeCard);
        fourCard = findViewById(R.id.fourCard);
        fiveCard = findViewById(R.id.fiveCard);

        oneCard.setOnClickListener(this);
        twoCard.setOnClickListener(this);
        treeCard.setOnClickListener(this);
        fourCard.setOnClickListener(this);
        fiveCard.setOnClickListener(this);

        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(getColor(R.color.Transparent));
        }
    }
    //Personalizacion manual Statusbar
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    //Para salir con seguridad.
    @Override
    public void onBackPressed(){
        if (presione + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Pulse otra vez para salir", Toast.LENGTH_LONG).show();
        presione = System.currentTimeMillis();
    }

    @Override
    public void onClick(View v) {
        Intent i ;
        switch (v.getId()){
            case R.id.oneCard : i = new Intent(this, Navegacion.class); startActivity(i); break;
            case R.id.twoCard : i = new Intent(this, Perfil.class); startActivity(i); break;
            case R.id.treeCard :
                Toast.makeText(MainActivity.this, "Catalogo Esta en Prueba", Toast.LENGTH_SHORT).show();
                i = new Intent(this, Catalogo.class); startActivity(i); break;
            case R.id.fourCard :
                Toast.makeText(MainActivity.this, "Realidad Aumentada Esta en Prueba", Toast.LENGTH_SHORT).show();
                i = new Intent(this, visionra.class); startActivity(i); break;
            case R.id.fiveCard :
                Toast.makeText(MainActivity.this, "Contacto por Culminarce", Toast.LENGTH_SHORT).show();
                i = new Intent(this, transport.class); startActivity(i); break;
            default: break;
        }
    }
}
