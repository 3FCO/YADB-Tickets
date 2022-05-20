package me.efco.yadbtickets.commands.normal;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.data.DBConnection;
import me.efco.yadbtickets.entities.Helper;
import me.efco.yadbtickets.entities.ServerInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OpenCommand extends AbstractCommand {
    public OpenCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        String description = event.getOption("description").getAsString();
        if (description.length() < 10) {
            event.reply("Your description must be more than 10 characters").setEphemeral(true).queue();
            return;
        }

        boolean isInfoValid = Helper.getInstance().isServerInfoValid(event.getGuild());
        if (!isInfoValid) {
            event.reply("``Ticket information invalid. Please tell and administrator``").setEphemeral(true).queue();
            try {
                int increment = DBConnection.getInstance().insertButtonInteractions("autosetup", event.getGuild().getId());
                MessageBuilder builder = new MessageBuilder()
                        .append("A user tried to use the ``/open`` command, but some channels/roles information isn't correct. Please use ``/setup`` command in server ``" + event.getGuild().getName() + "``\n")
                        .append("You you like me to setup for you?\n")
                        .append("This message self-destroys <t:" + Instant.now().plus(5, ChronoUnit.MINUTES).getEpochSecond() + ":R>")
                        .setActionRows(
                                ActionRow.of(
                                        Button.primary(increment+"", "Yes, create all necessary stuff"),
                                        Button.secondary("delete", "No, I'll do it myself")
                                )
                        );

                Message message = event.getGuild().retrieveOwner().submit().get().getUser().openPrivateChannel().submit().get().sendMessage(builder.build()).submit().get();
                message.delete().queueAfter(5, TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return;
        }

        if (DBConnection.getInstance().getActiveTickets(event.getUser().getIdLong()) >= 3) {
            event.reply("You have 3 or more active tickets, and therefore you cannot create another one").setEphemeral(true).queue();
            return;
        }

        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(event.getGuild().getIdLong());

        int ticketId = DBConnection.getInstance().insertTicket(event.getUser().getIdLong());
        Category category = event.getGuild().getCategoryById(serverInfo.getCategoryId());
        MessageBuilder messageBuilder = new MessageBuilder()
                .setEmbeds(new EmbedBuilder()
                        .setTitle("Ticket #" + String.format("%05d", ticketId))
                        .addField("Author", event.getUser().getAsTag(), false)
                        .addField("Information", description, false).build()).allowMentions(Message.MentionType.USER);

        TextChannel channel;
        try {
            channel = category.createTextChannel("ticket-" + String.format("%05d", ticketId))
                    .syncPermissionOverrides().submit().get();

            channel.sendMessage(messageBuilder.build()).queue();
            event.reply("A support channel has been opened in your name. Here it is " + channel.getAsMention()).queue();
            event.getGuild().getTextChannelById(serverInfo.getChannelId()).sendMessage("``A new support ticket has been created``\n" + channel.getAsMention()).queue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
