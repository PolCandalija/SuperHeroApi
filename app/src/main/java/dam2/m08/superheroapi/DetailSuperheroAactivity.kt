package dam2.m08.superheroapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import dam2.m08.superheroapi.databinding.ActivityDetailSuperheroBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class DetailSuperheroActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private lateinit var binding: ActivityDetailSuperheroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSuperheroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id: String = intent.getStringExtra(EXTRA_ID).orEmpty()
        getSuperheroInformation(id)
    }

    private fun getSuperheroInformation(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val superheroDetail =
                getRetrofit().create(ApiService::class.java).getSuperheroDetail(id)

            if(superheroDetail.body() != null){
                runOnUiThread { createUI(superheroDetail.body()!!) }
            }
        }
    }

    private fun createUI(superhero: SuperHeroDetailResponse) {
        Picasso.get().load(superhero.image.url).into(binding.ivSuperhero)
        binding.tvSuperheroName.text = superhero.name
        prepareStats(superhero.powerstats)
        binding.tvSuperheroRealName.text = superhero.biography.fullName
        binding.tvPublisher.text = superhero.biography.publisher
    }

    private fun prepareStats(powerstats: PowerStatsResponse) {
        if(powerstats.combat != null)
            updateHeight(binding.viewCombat, powerstats.combat)
        if(powerstats.durability != null)
            updateHeight(binding.viewDurability, powerstats.durability)
        if(powerstats.speed != null)
            updateHeight(binding.viewSpeed, powerstats.speed)
        if(powerstats.strength != null)
            updateHeight(binding.viewStrength, powerstats.strength)
        if(powerstats.intelligence != null)
            updateHeight(binding.viewIntelligence, powerstats.intelligence)
        if(powerstats.power != null)
            updateHeight(binding.viewPower, powerstats.power)
    }

    private fun updateHeight(view: View, stat:String){
        try {
            if(view.layoutParams != null)
            {
                val params = view.layoutParams
                if(stat.isNotEmpty() && stat != null && stat != "")
                {
                    params.height = pxToDp(stat.toFloat())
                }
                view.layoutParams = params
            }
        } catch (e: Exception) {
            println("PROVA POL")
        }


    }

    private fun pxToDp(px:Float):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics).roundToInt()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://superheroapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}