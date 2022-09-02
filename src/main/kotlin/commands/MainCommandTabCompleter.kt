package jp.faketuna.paper.deeplchattranslator.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class MainCommandTabCompleter: TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
        if(command.name.equals("deeplchattranslator", ignoreCase = true ) && args!!.size == 1){
            val list = mutableListOf<String>()
            list.add("version")
            list.add("toggle")
            list.add("enable")
            list.add("disable")
            return list
        }
        return null
    }
}