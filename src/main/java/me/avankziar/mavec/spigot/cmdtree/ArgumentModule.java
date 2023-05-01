package main.java.me.avankziar.mavec.spigot.cmdtree;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import main.java.me.avankziar.mavec.spigot.MAVEC;

public abstract class ArgumentModule
{
	public ArgumentConstructor argumentConstructor;

    public ArgumentModule(ArgumentConstructor argumentConstructor)
    {
       this.argumentConstructor = argumentConstructor;
       MAVEC.getPlugin().getArgumentMap().put(argumentConstructor.getPath(), this);
    }
    
    //This method will process the command.
    public abstract void run(CommandSender sender, String[] args) throws IOException;

}
