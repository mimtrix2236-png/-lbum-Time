package com.albumtime.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import android.net.Uri

private val Ink = Color(0xFF191624)
private val Violet = Color(0xFF7C4DFF)
private val Soft = Color(0xFFF4F1FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AlbumTimeApp() }
    }
}

data class Template(val title:String, val subtitle:String, val category:String, val emoji:String)
private val templates = listOf(
    Template("Fondo verde / recorte", "Separá a la persona y cambiá el escenario", "Recortes", "🟩"),
    Template("Disfraz de diablita", "Cuernos, capa y accesorios rojos", "Disfraces", "😈"),
    Template("Peluca de colores", "Probá un look de cabello diferente", "Estilos", "💜"),
    Template("Look colegial", "Camisa, corbata y estilo alternativo", "Disfraces", "👔"),
    Template("Retrato de cuerpo entero", "Composición con pose y fondo", "Retratos", "📸"),
    Template("Verano y piscina", "Fondos luminosos y ambiente veraniego", "Fondos", "🌴"),
    Template("Collage", "Uní varias fotos y recortes", "Composición", "🖼️"),
    Template("Romántica", "Colores suaves, flores y corazones", "Estilos", "💗")
)

@Composable
fun AlbumTimeApp() {
    var page by remember { mutableStateOf("home") }
    var selectedTemplate by remember { mutableStateOf<Template?>(null) }
    var photo by remember { mutableStateOf<Uri?>(null) }
    var category by remember { mutableStateOf("Todas") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if(uri!=null){photo=uri;page="editor"} }
    BackHandler(page!="home") { page="home" }

    MaterialTheme(colorScheme = lightColorScheme(primary=Violet, background=Soft, surface=Color.White)) {
        when(page) {
            "templates" -> TemplatesScreen(category, {category=it}, onBack={page="home"}, onChoose={selectedTemplate=it;page="templatePreview"}, onPhoto={picker.launch("image/*")})
            "templatePreview" -> TemplatePreview(selectedTemplate, onBack={page="templates"}, onUse={picker.launch("image/*")})
            "editor" -> EditorScreen(photo, onBack={page="home"}, onChangePhoto={picker.launch("image/*")})
            else -> HomeScreen(onTemplates={page="templates"}, onImport={picker.launch("image/*")})
        }
    }
}

@Composable
fun HomeScreen(onTemplates:()->Unit, onImport:()->Unit) {
    Column(Modifier.fillMaxSize().background(Soft).padding(20.dp)) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(54.dp).background(Violet, RoundedCornerShape(18.dp)), contentAlignment=Alignment.Center) { Text("AT",color=Color.White,fontWeight=FontWeight.Black) }
            Spacer(Modifier.width(12.dp))
            Column { Text("Album Time", style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold,color=Ink); Text("Tu creatividad, en una foto",color=Color.Gray) }
        }
        Spacer(Modifier.height(26.dp))
        Text("¿Qué querés crear hoy?",style=MaterialTheme.typography.headlineMedium,fontWeight=FontWeight.Bold,color=Ink)
        Spacer(Modifier.height(14.dp))
        ActionCard("Editar una foto","Elegí una imagen de tu teléfono","📷",onImport)
        Spacer(Modifier.height(12.dp))
        ActionCard("Explorar plantillas","Disfraces, fondos, recortes y estilos","✨",onTemplates)
        Spacer(Modifier.height(24.dp))
        Text("Plantillas destacadas",fontWeight=FontWeight.Bold,color=Ink,style=MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        templates.take(4).forEach { t ->
            TemplateRow(t) { onTemplates() }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ActionCard(title:String,subtitle:String,emoji:String,action:()->Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White,RoundedCornerShape(20.dp)).clickable{action()}.padding(18.dp),verticalAlignment=Alignment.CenterVertically) {
        Text(emoji,style=MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) { Text(title,fontWeight=FontWeight.Bold,color=Ink); Text(subtitle,color=Color.Gray,style=MaterialTheme.typography.bodyMedium) }
        Icon(Icons.Default.ChevronRight,contentDescription=null,tint=Violet)
    }
}
@Composable
fun TemplatesScreen(category:String,onCategory:(String)->Unit,onBack:()->Unit,onChoose:(Template)->Unit,onPhoto:()->Unit) {
    val categories=listOf("Todas","Recortes","Disfraces","Estilos","Retratos","Fondos","Composición")
    Column(Modifier.fillMaxSize().background(Soft).padding(16.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) { IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,null)}; Text("Plantillas",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold) }
        Text("Elegí un estilo o empezá con tu foto",color=Color.Gray)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) { Button(onClick=onPhoto){Text("＋ Foto propia")}; OutlinedButton(onClick=onCategoryInfo){Text("Mis plantillas")} }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)) {
            item { Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) { categories.forEach { c -> FilterChip(selected=category==c,onClick={onCategory(c)},label={Text(c)}) } } }
            items(templates.filter{category=="Todas"||it.category==category}) { TemplateRow(it){onChoose(it)} }
        }
    }
}
private fun onCategoryInfo() {}
@Composable
fun TemplateRow(template:Template,onClick:()->Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White,RoundedCornerShape(18.dp)).clickable{onClick()}.padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
        Box(Modifier.size(64.dp).background(Color(0xFFE9E2FF),RoundedCornerShape(14.dp)),contentAlignment=Alignment.Center){Text(template.emoji,style=MaterialTheme.typography.headlineMedium)}
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) { Text(template.title,fontWeight=FontWeight.Bold,color=Ink);Text(template.subtitle,style=MaterialTheme.typography.bodySmall,color=Color.Gray);Text(template.category,style=MaterialTheme.typography.labelSmall,color=Violet) }
        Icon(Icons.Default.ChevronRight,null,tint=Violet)
    }
}
@Composable
fun TemplatePreview(template:Template?,onBack:()->Unit,onUse:()->Unit) {
    Column(Modifier.fillMaxSize().background(Soft).padding(18.dp)) {
        IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,null)}
        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().weight(1f).background(Color.White,RoundedCornerShape(24.dp)),contentAlignment=Alignment.Center) {
            Column(horizontalAlignment=Alignment.CenterHorizontally) { Text(template?.emoji ?: "✨",style=MaterialTheme.typography.displayLarge);Text(template?.title ?: "Plantilla",fontWeight=FontWeight.Bold);Text("Vista previa del estilo",color=Color.Gray) }
        }
        Spacer(Modifier.height(14.dp))
        Text(template?.subtitle ?: "",color=Ink)
        Spacer(Modifier.height(12.dp))
        Button(onClick=onUse,modifier=Modifier.fillMaxWidth()) { Text("Usar con mi foto") }
    }
}
@Composable
fun EditorScreen(uri:Uri?,onBack:()->Unit,onChangePhoto:()->Unit) {
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    Column(Modifier.fillMaxSize().background(Soft).padding(16.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) { IconButton(onClick=onBack){Icon(Icons.Default.ArrowBack,null)};Text("Editor",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold);Spacer(Modifier.weight(1f));TextButton(onClick=onChangePhoto){Text("Cambiar foto")} }
        Box(Modifier.fillMaxWidth().weight(1f).background(Color.Black,RoundedCornerShape(18.dp)),contentAlignment=Alignment.Center) {
            if(uri!=null) AsyncImage(model=uri,contentDescription="Foto seleccionada",modifier=Modifier.fillMaxSize())
            else Text("Importá una imagen",color=Color.White)
        }
        Text("Ajustes básicos",fontWeight=FontWeight.Bold)
        SliderRow("Brillo",brightness,-1f..1f,{brightness=it})
        SliderRow("Contraste",contrast,0f..2f,{contrast=it})
        SliderRow("Saturación",saturation,0f..2f,{saturation=it})
        Button(onClick={ /* export is added in next implementation stage */ },modifier=Modifier.fillMaxWidth()) { Text("Guardar copia") }
    }
}
@Composable
fun SliderRow(label:String,value:Float,range:ClosedFloatingPointRange<Float>,onValue:(Float)->Unit) {
    Row(verticalAlignment=Alignment.CenterVertically) { Text(label,Modifier.width(76.dp));Slider(value=value,onValueChange=onValue,valueRange=range,modifier=Modifier.weight(1f)) }
}
