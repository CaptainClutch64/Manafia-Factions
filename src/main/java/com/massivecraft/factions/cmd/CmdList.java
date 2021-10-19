package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagUtil;

import java.util.ArrayList;
import java.util.List;


public class CmdList extends FCommand {

    /**
     * @author FactionsUUID Team - Modified By CmdrKittens
     */

    private final String[] defaults = new String[3];

    public CmdList() {
        super();
        this.aliases.addAll(Aliases.list);

        // default values in case user has old config
        defaults[0] = "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------";
        defaults[1] = "<i>Factionless<i> {factionless} online";
        defaults[2] = "<a>{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}";

        //this.requiredArgs.add("");
        this.optionalArgs.put("page", "1");

        this.requirements = new CommandRequirements.Builder(Permission.LIST)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostList, "to list the factions", "for listing the factions"))
            return;

        ArrayList<Faction> factionList = Factions.getInstance().getAllFactions();
        for (int i = 0; i >= -2; i--)
            factionList.remove(Factions.getInstance().getFactionById(String.valueOf(i)));

        // remove exempt factions
        if (context.fPlayer != null && context.fPlayer.getPlayer() != null && !context.fPlayer.getPlayer().hasPermission("factions.show.bypassexempt")) {
            List<String> exemptFactions = FactionsPlugin.getInstance().getConfig().getStringList("show-exempt");

            factionList.removeIf(next -> exemptFactions.contains(next.getTag()));
        }

        // Sort by total followers first
        factionList.sort((f1, f2) -> {
            int f1Size = f1.getFPlayers().size();
            int f2Size = f2.getFPlayers().size();
            if (f1Size < f2Size)
                return 1;
            else if (f1Size > f2Size)
                return -1;
            return 0;
        });

        // Then sort by how many members are online now
        factionList.sort((f1, f2) -> {
            int f1Size = f1.getFPlayersWhereOnline(true).size();
            int f2Size = f2.getFPlayersWhereOnline(true).size();
            if (f1Size < f2Size)
                return 1;
            else if (f1Size > f2Size)
                return -1;
            return 0;
        });

        ArrayList<String> lines = new ArrayList<>();

        factionList.add(0, Factions.getInstance().getWilderness());

        final int pageheight = 9;
        int pagenumber = context.argAsInt(0, 1);
        int pagecount = (factionList.size() / pageheight) + 1;
        if (pagenumber > pagecount)
            pagenumber = pagecount;
        else if (pagenumber < 1)
            pagenumber = 1;
        int start = (pagenumber - 1) * pageheight;
        int end = start + pageheight;
        if (end > factionList.size())
            end = factionList.size();


        String header = FactionsPlugin.getInstance().getConfig().getString("list.header", defaults[0]);
        assert header != null;
        header = header.replace("{pagenumber}", String.valueOf(pagenumber)).replace("{pagecount}", String.valueOf(pagecount));
        lines.add(FactionsPlugin.getInstance().txt.parse(header));

        for (Faction faction : factionList.subList(start, end)) {
            if (faction.isWilderness()) {
                lines.add(FactionsPlugin.getInstance().txt.parse(TagUtil.parsePlain(faction, FactionsPlugin.getInstance().getConfig().getString("list.factionless", defaults[1]))));
                continue;
            }
            lines.add(FactionsPlugin.getInstance().txt.parse(TagUtil.parsePlain(faction, context.fPlayer, FactionsPlugin.getInstance().getConfig().getString("list.entry", defaults[2]))));
        }
        context.sendMessage(lines);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LIST_DESCRIPTION;
    }
}