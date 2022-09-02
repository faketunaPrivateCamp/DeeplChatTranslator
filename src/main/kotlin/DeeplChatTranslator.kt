package jp.faketuna.paper.deeplchattranslator

import com.fasterxml.jackson.databind.ObjectMapper
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStream
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL

class DeeplChatTranslator: JavaPlugin(), Listener {

    private lateinit var pluginConfig:FileConfiguration
    private var apiKey: String? = null

    override fun onEnable() {
        loadPluginConfig()
        server.pluginManager.registerEvents(this, this)

        logger.info("plugin loaded!")
    }

    override fun onDisable() {
        logger.info("plugin unloaded!")
    }

    @EventHandler
    fun onChat(e: AsyncChatEvent){
        e.isCancelled = true
        val component:TextComponent = e.originalMessage() as TextComponent
        val playerName = e.player.name() as TextComponent
        val translated = invokeWebRequest(component.content(), "JA")
        for (p in Bukkit.getOnlinePlayers()){
            p.sendMessage(Component.text("${playerName.content()}: $translated").hoverEvent(HoverEvent.showText(component.color(NamedTextColor.WHITE))))
        }
        server.logger.info("${playerName.content()}: ${component.content()} | $translated")
    }


    private fun invokeWebRequest(text: String, targetLang: String): String {
        val url = URL("https://api-free.deepl.com/v2/translate")
        val postData = "text=$text&target_lang=$targetLang"
        with(url.openConnection() as HttpURLConnection){
            requestMethod = "POST"
            connectTimeout = 1000
            doOutput = true
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            setRequestProperty("Content-Length", postData.length.toString())
            setRequestProperty("Authorization", apiKey)
            setRequestProperty("User-Agent", "DCT/1.0.0")
            useCaches = false

            with(PrintStream(outputStream)){
                this.print(postData)
                this.close()
            }
            outputStream.close()

            if (responseCode != HttpURLConnection.HTTP_OK) return "§4ERROR: FAILED TO FETCH"

            if (responseCode == HttpURLConnection.HTTP_OK){
                val inps: InputStream = inputStream
                val mapper = ObjectMapper()
                val root = mapper.readTree(inps)
                val json = root.get("translations").toString().replace("[", "").replace("]", "")
                return mapper.readTree(json).get("text").asText()
            }
        }
        return "ERROR: FAILED TO GET STRING"
    }


    private fun loadPluginConfig(){
        val file = File(this.dataFolder, "key.yml")
        val exists = file.exists()
        if (!exists){
            try{
                file.createNewFile()
            } catch (_: Exception){}
        }

        pluginConfig = YamlConfiguration.loadConfiguration(file)

        if (!exists){
            pluginConfig.set("apiKey", "YOUR DEEPL API KEY")
            savePluginConfig()
        }

        apiKey = pluginConfig.getString("apiKey")
    }

    private fun savePluginConfig(){
        val file = File(this.dataFolder, "key.yml")
        pluginConfig.save(file)
    }
}