package me.efco.yadbtickets.commands.normal;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.data.DBConnection;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public class StatCommand extends AbstractCommand {
    public StatCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        Member member = event.getOption("target").getAsMember();
        int amount = DBConnection.getInstance().getSolvedTicketsByadmin(member.getIdLong());

        event.reply(member.getEffectiveName() + " has solved #" + amount + " tickets").setEphemeral(true).queue();
    }
}
