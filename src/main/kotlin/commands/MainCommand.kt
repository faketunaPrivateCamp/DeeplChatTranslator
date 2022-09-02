package jp.faketuna.paper.deeplchattranslator.commands

import jp.faketuna.paper.deeplchattranslator.DeeplChatTranslator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class MainCommand: CommandExecutor {

    private val prefix = DeeplChatTranslator.PluginManager.getPluginPrefix()
    private val pluginManager = DeeplChatTranslator.PluginManager
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args!!.isEmpty()){
            sender.sendMessage("USAGE")
            return true
        }

        if(command.name.equals("deeplchattranslator", ignoreCase = true)){
            if(args[0].equals("version", ignoreCase = true)){
                sender.sendMessage("$prefix This server is currently running version ${DeeplChatTranslator.PluginManager.getPluginVerison()}")
            }

            if(args[0].equals("toggle", ignoreCase = true)){
                pluginManager.setTranslationState(!pluginManager.getTranslationState())
                sender.sendMessage("$prefix Plugin chat translation is now ${pluginManager.getTranslationState()}.")
            }

            if(args[0].equals("enable", ignoreCase = true)){
                if (pluginManager.getTranslationState()){
                    sender.sendMessage("$prefix Chat translation is already enabled!")
                    return true
                }
                pluginManager.setTranslationState(true)
                sender.sendMessage("$prefix Enabled chat translation")
            }

            if(args[0].equals("disable", ignoreCase = true)){
                if (!pluginManager.getTranslationState()){
                    sender.sendMessage("$prefix Chat translation is already disabled!")
                    return true
                }
                pluginManager.setTranslationState(false)
                sender.sendMessage("$prefix Disabled chat translation")
            }
            return true
        }
        return false
    }
}