package com.albumtime.app

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val Ink = Color(0xFF191624)
private val Violet = Color(0xFF7C4DFF)
private val Soft = Color(0xFFF4F1FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { AlbumTimeApp() } }
}

data class Template(val title:String, val description:String, val category:String, val emoji:String)
private val templateList = listOf(
    Template("Fondo verde / recorte","Prepará una foto para quitar o reemplazar el fondo","Recortes","🟩"),
    Template("Disfraz de diablita","Idea de montaje con accesorios rojos","Disfraces","😈"),
    Template("Peluca de colores","Probá un look de cabello colorido","Estilos","💜"),
    Template("Look colegial","Estilo camisa y corbata","Disfraces","👔"),
    Template("Retrato de cuerpo entero","Composición de retrato","Retratos","📸"),
    Template("Verano / piscina","Estilo luminoso de verano","Fondos","🌴"),
    Template("Collage","Armá una composición con varias fotos","Composición","🖼️"),
    Template("Romántica","Tonos suaves, flores y corazones","Estilos","💗"),
    Template("Anime","Estética ilustrada","Estilos","🌸"),
    Template("Naturaleza","Fondos de parque y paisajes","Fondos","🌿")
)

@Composable
fun AlbumTimeApp() {
    var page by remember { mutableStateOf("home") }
    var pickedTemplate by remember { mutableStateOf<Template?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if(uri != null) { photoUri=uri; page="editor" }
    }
    BackHandler(page!="home") { page="home" }
    MaterialTheme(colorScheme=lightColorScheme(primary=Violet,background=Soft,surface=Color.White)) {
        when(page) {
            "templates" -> TemplateScreen(onBack={page="home"},onTemplate={pickedTemplate=it;picker.launch("image/*")},onImport={pickedTemplate=null;picker.launch("image/*")})
            "editor" -> EditorScreen(photoUri,pickedTemplate,onBack={page="home"},onChangePhoto={picker.launch("image/*")})
            else -> HomeScreen(onImport={pickedTemplate=null;picker.launch("image/*")},onTemplates={page="templates"})
        }
    }
}

@Composable
fun HomeScreen(onImport:()->Unit,onTemplates:()->Unit) {
    Column(Modifier.fillMaxSize().background(Soft).padding(20.dp)) {
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(54.dp).background(Violet,RoundedCornerShape(18.dp)),contentAlignment=Alignment.Center){Text("AT",color=Color.White,fontWeight=FontWeight.Black)}
            Spacer(Modifier.width(12.dp)); Column { Text("AlbumTime",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,color=Ink);Text("Tu creatividad, en una foto",color=Color.Gray) }
        }
        Spacer(Modifier.height(30.dp)); Text("¿Qué querés crear hoy?",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=Ink)
        Spacer(Modifier.height(14.dp))
        ActionCard("Editar una foto","Abrí una imagen y ajustala","📷",onImport)
        Spacer(Modifier.height(12.dp));ActionCard("Explorar plantillas","Estilos, fondos y composiciones","✨",onTemplates)
        Spacer(Modifier.height(24.dp));Text("Plantillas",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleLarge,color=Ink)
        Spacer(Modifier.height(8.dp));templateList.take(4).forEach { TemplateRow(it,onTemplates);Spacer(Modifier.height(8.dp)) }
    }
}
@Composable
fun ActionCard(title:String,sub:String,emoji:String,action:()->Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White,RoundedCornerShape(20.dp)).clickable{action()}.padding(18.dp),verticalAlignment=Alignment.CenterVertically) {
        Text(emoji,style=MaterialTheme.typography.headlineLarge);Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold,color=Ink);Text(sub,color=Color.Gray)}
        Icon(Icons.Default.ChevronRight,null,tint=Violet)
    }
}
@Composable
fun TemplateScreen(onBack:()->Unit,onTemplate:(Template)->Unit,onImport:()->Unit) {
    var cat by remember { mutableStateOf("Todas") }
    val categories=listOf("Todas","Recortes","Disfraces","Estilos","Retratos","Fondos","Composición")
    Column(Modifier.fillMaxSize().background(Soft).padding(16.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,null)};Text("Plantillas",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)}
        Text("Elegí un estilo para empezar con tu foto",color=Color.Gray);Spacer(Modifier.height(8.dp))
        Button(onClick=onImport,modifier=Modifier.fillMaxWidth()){Text("＋ Foto propia")}
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)) {
            item { Row { categories.take(4).forEach { c -> FilterChip(selected=cat==c,onClick={cat=c},label={Text(c)}) } } }
            item { Row { categories.drop(4).forEach { c -> FilterChip(selected=cat==c,onClick={cat=c},label={Text(c)}) } } }
            items(templateList.filter{cat=="Todas"||it.category==cat}) { t -> TemplateRow(t){onTemplate(t)} }
        }
    }
}
@Composable
fun TemplateRow(t:Template,onClick:()->Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White,RoundedCornerShape(18.dp)).clickable{onClick()}.padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
        Box(Modifier.size(60.dp).background(Color(0xFFE9E2FF),RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center){Text(t.emoji,style=MaterialTheme.typography.headlineMedium)}
        Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(t.title,fontWeight=FontWeight.Bold,color=Ink);Text(t.description,style=MaterialTheme.typography.bodySmall,color=Color.Gray);Text(t.category,style=MaterialTheme.typography.labelSmall,color=Violet)}
        Icon(Icons.Default.ChevronRight,null,tint=Violet)
    }
}

@Composable
fun EditorScreen(uri:Uri?,template:Template?,onBack:()->Unit,onChangePhoto:()->Unit) {
    val context= LocalContext.current
    var original by remember { mutableStateOf<Bitmap?>(null) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    LaunchedEffect(uri) { original = uri?.let { withContext(Dispatchers.IO){ context.contentResolver.openInputStream(it)?.use{stream->BitmapFactory.decodeStream(stream)} } };brightness=0f;contrast=1f;saturation=1f;rotation=0 }
    val edited=remember(original,brightness,contrast,saturation,rotation) { original?.let { adjustBitmap(it,brightness,contrast,saturation,rotation) } }
    Column(Modifier.fillMaxSize().background(Soft).padding(12.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,null)};Text("Editor",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleLarge);Spacer(Modifier.weight(1f));TextButton(onClick=onChangePhoto){Text("Cambiar foto")}}
        if(template!=null) Text("Plantilla: ${template.title}",color=Violet,fontWeight=FontWeight.SemiBold)
        Box(Modifier.fillMaxWidth().weight(1f).background(Color.Black,RoundedCornerShape(16.dp)),contentAlignment=Alignment.Center) {
            edited?.let { androidx.compose.foundation.Image(it.asImageBitmap(),contentDescription="Vista previa editada",modifier=Modifier.fillMaxSize()) } ?: Text("Seleccioná una foto",color=Color.White)
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){TextButton(onClick={rotation=(rotation+90)%360}){Icon(Icons.Default.RotateRight,null);Text("Rotar")};TextButton(onClick={brightness=0f;contrast=1f;saturation=1f;rotation=0}){Text("Restablecer")}}
        Text("Ajustes",fontWeight=FontWeight.Bold)
        SliderRow("Brillo",brightness,-1f..1f,{brightness=it})
        SliderRow("Contraste",contrast,0f..2f,{contrast=it})
        SliderRow("Color",saturation,0f..2f,{saturation=it})
        Button(enabled=edited!=null&&!busy,onClick={busy=true;val bmp=edited; if(bmp!=null){saveBitmap(context,bmp){busy=false;Toast.makeText(context,if(it)"Guardada en Galería" else "No se pudo guardar",Toast.LENGTH_LONG).show()}}},modifier=Modifier.fillMaxWidth()) {Text(if(busy)"Guardando…" else "Guardar copia en Galería")}
    }
}
@Composable
fun SliderRow(label:String,value:Float,range:ClosedFloatingPointRange<Float>,onValue:(Float)->Unit) {
    Row(verticalAlignment=Alignment.CenterVertically){Text(label,Modifier.width(68.dp));Slider(value=value,onValueChange=onValue,valueRange=range,modifier=Modifier.weight(1f))}
}
private fun adjustBitmap(src:Bitmap,b:Float,c:Float,s:Float,rot:Int):Bitmap {
    val matrix=ColorMatrix().apply { setSaturation(s); val scale=c; val translate=128f*(1-scale)+b*255f; postConcat(ColorMatrix(floatArrayOf(scale,0f,0f,0f,translate,0f,scale,0f,0f,translate,0f,0f,scale,0f,translate,0f,0f,0f,1f,0f))) }
    val rotated=if(rot==0)src else Bitmap.createBitmap(src,0,0,src.width,src.height,Matrix().apply{postRotate(rot.toFloat())},true)
    val out=Bitmap.createBitmap(rotated.width,rotated.height,Bitmap.Config.ARGB_8888)
    Canvas(out).drawBitmap(rotated,0f,0f,Paint().apply{colorFilter=ColorMatrixColorFilter(matrix)})
    return out
}
private fun saveBitmap(context:android.content.Context,bitmap:Bitmap,callback:(Boolean)->Unit) {
    Thread {
        var success=false
        try {
            val values=ContentValues().apply { put(MediaStore.Images.Media.DISPLAY_NAME,"AlbumTime_${System.currentTimeMillis()}.jpg");put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/AlbumTime") }
            val uri=context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
            if(uri!=null) context.contentResolver.openOutputStream(uri)?.use { success=bitmap.compress(Bitmap.CompressFormat.JPEG,95,it) }
        } catch(_:Exception) {}
        (context as? ComponentActivity)?.runOnUiThread { callback(success) }
    }.start()
}
