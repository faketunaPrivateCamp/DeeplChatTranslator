package jp.faketuna.paper.deeplchattranslator

import com.fasterxml.jackson.databind.ObjectMapper
import io.papermc.paper.event.player.AsyncChatEvent
import jp.faketuna.paper.deeplchattranslator.commands.MainCommand
import jp.faketuna.paper.deeplchattranslator.commands.MainCommandTabCompleter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
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

    object PluginManager{
        private const val pluginVersion = "1.0.0"
        private const val pluginPrefix = "§6[DCT]§r"
        private var translationState = false
        private var apiKey: String? = null

        fun getPluginVerison(): String { return this.pluginVersion }
        fun getPluginPrefix(): String { return this.pluginPrefix }

        fun setTranslationState(b: Boolean) { this.translationState = b }
        fun getTranslationState(): Boolean { return this.translationState }

        fun setApiKey(apiKey: String?) { this.apiKey = apiKey }
        fun getApiKey(): String? { return this.apiKey }
    }

    override fun onEnable() {
        loadPluginConfig()
        server.pluginManager.registerEvents(this, this)
        this.getCommand("deeplchattranslator")!!.setExecutor(MainCommand())
        this.getCommand("deeplchattranslator")!!.setTabCompleter(MainCommandTabCompleter())
        logger.info("plugin loaded!")
    }

    override fun onDisable() {
        logger.info("plugin unloaded!")
    }

    @EventHandler
    fun onChat(e: AsyncChatEvent){
        if(PluginManager.getTranslationState()) {
            val component: TextComponent = e.originalMessage() as TextComponent
            val translated = invokeWebRequest(component.content(), "JA")
            e.message(Component.text(translated).hoverEvent(HoverEvent.showText(component.color(NamedTextColor.WHITE))))
        }
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
            setRequestProperty("Authorization", PluginManager.getApiKey())
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
        val file = File(this.dataFolder, "config.yml")
        val exists = file.exists()
        if (!exists){
            try{
                file.createNewFile()
            } catch (_: Exception){}
        }

        pluginConfig = YamlConfiguration.loadConfiguration(file)

        if (!exists){
            pluginConfig.set("apiKey", "YOUR DEEPL API KEY")
            pluginConfig.set("enabled", false)
            savePluginConfig()
        }

        PluginManager.setApiKey(pluginConfig.getString("apiKey"))
        PluginManager.setTranslationState(pluginConfig.getBoolean("enabled"))
    }

    private fun savePluginConfig(){
        val file = File(this.dataFolder, "config.yml")
        pluginConfig.save(file)
    }
}