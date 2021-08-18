package net.lxsthw.friends.bukkit.cmd;

import net.lxsthw.friends.database.Database;
import net.lxsthw.friends.profile.Profile;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.slicecollections.maxteer.player.role.Role;

public class FriendsCommand extends Commands {
  public FriendsCommand() {
    super("amigo", new String[]{"friend"});
  }

  public void perform(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      Profile profile = Profile.createOrLoadProfile(player.getName());
      if (args.length == 0) {
        this.sendHelp(player);
      } else {
        String action = args[0];
        String target;
        Profile ptarget;
        if (action.equalsIgnoreCase("aceitar")) {
          if (args.length < 2) {
            player.sendMessage("§cUtilize /amigo aceitar [jogador]");
          } else {
            target = args[1];
            if (target.equalsIgnoreCase(player.getName())) {
              player.sendMessage("§cVocê não pode aceitar convites de você mesmo.");
            } else {
              ptarget = Profile.loadIfExists(target);
              if (ptarget == null) {
                player.sendMessage("§cUsuário não encontrado.");
              } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                player.sendMessage("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cestá na sua lista negra.");
              } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                player.sendMessage("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c.");
              } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
                player.sendMessage("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + " já são amigos.");
              } else if (!ptarget.getRequests().isRequested(profile.getPlayerName())) {
                player.sendMessage("§cEste usuário não enviou nenhuma solicitação para você.");
              } else {
                profile.getFriends().addFriend(ptarget.getPlayerName());
                ptarget.getFriends().addFriend(profile.getPlayerName());
                ptarget.getRequests().removeRequest(profile.getPlayerName());
                profile.save();
                ptarget.save();
                player.sendMessage(" \n §eO jogador §f" + Role.getPrefixed(ptarget.getPlayerName()) + "§e é agora seu amigo!\n ");
                if (!ptarget.isCurrentOnline()) {
                  ptarget.destroy();
                } else {
                  ptarget.sendMessage(" \n §f" + Role.getPrefixed(profile.getPlayerName()) + " §aaceitou sua solicitação de amizade.\n ");
                }
              }

              ptarget = null;
            }
          }
        } else if (!action.equalsIgnoreCase("add")) {
          if (action.equalsIgnoreCase("ajuda")) {
            this.sendHelp(player);
          } else if (action.equalsIgnoreCase("cancelar")) {
            if (args.length < 2) {
              player.sendMessage("§cUtilize /amigo cancelar [jogador]");
            } else {
              target = args[1];
              if (target.equalsIgnoreCase(player.getName())) {
                player.sendMessage("§cVocê não pode cancelar solicitações para você mesmo.");
              } else {
                ptarget = Profile.loadIfExists(target);
                if (ptarget == null) {
                  player.sendMessage("§cUsuário não encontrado.");
                } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                  player.sendMessage("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cestá na sua lista negra.");
                } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                  player.sendMessage("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c.");
                } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
                  player.sendMessage("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + " §cjá são amigos.");
                } else if (!profile.getRequests().isRequested(ptarget.getPlayerName())) {
                  player.sendMessage("§cVocê não enviou nenhuma solicitação de amizade para " + Role.getPrefixed(ptarget.getPlayerName() + "."));
                } else {
                  profile.getRequests().removeRequest(ptarget.getPlayerName());
                  profile.save();
                  player.sendMessage(" \n §aVocê cancelou sua solicitação de amizade para §f" + Role.getPrefixed(ptarget.getPlayerName()) + "§a.\n ");
                  if (!ptarget.isCurrentOnline()) {
                    ptarget.destroy();
                  } else {
                    ptarget.sendMessage(" \n §f" + Role.getPrefixed(profile.getPlayerName()) + " §acancelou a solicitação de amizade destinada a você.\n ");
                  }
                }

                ptarget = null;
              }
            }
          } else if (action.equalsIgnoreCase("desbloquear")) {
            if (args.length < 2) {
              player.sendMessage("§cUtilize /amigo desbloquear [jogador]");
            } else {
              target = args[1];
              ptarget = Profile.loadIfExists(target);
              if (target.equalsIgnoreCase(player.getName())) {
                player.sendMessage("§cVocê não pode desbloquear você mesmo.");
              } else if (ptarget == null) {
                player.sendMessage("§cUsuário não encontrado.");
              } else if (!profile.getBlackList().isInBlackList(Role.getPrefixed(ptarget.getPlayerName()))) {
                player.sendMessage("§c" + ptarget.getPlayerName() + " §cnão está na sua lista negra.");
              } else {
                profile.getBlackList().removeFromBlackList(ptarget.getPlayerName());
                profile.save();
                player.sendMessage(" \n §aVocê removeu §f" + Role.getPrefixed(ptarget.getPlayerName()) + " §ada sua lista negra.\n ");
                if (!ptarget.isCurrentOnline()) {
                  ptarget.destroy();
                }
              }
            }
          } else {
            int page;
            if (action.equalsIgnoreCase("listar")) {
              page = 1;
              if (args.length > 1) {
                try {
                  page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var18) {
                }
              }

              if (page < 1) {
                player.sendMessage("§cUtilize um número válido.");
              } else {
                this.listFriends(player, profile, page);
              }
            } else if (action.equalsIgnoreCase("excluir")) {
              if (args.length < 2) {
                player.sendMessage("§cUtilize /amigo excluir [jogador]");
              } else {
                target = args[1];
                if (target.equalsIgnoreCase(player.getName())) {
                  player.sendMessage("§cVocê não pode excluir você mesmo.");
                } else {
                  ptarget = Profile.loadIfExists(target);
                  if (ptarget == null) {
                    player.sendMessage("§cUsuário não encontrado.");
                  } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                    player.sendMessage("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cestá na sua lista negra.");
                  } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                    player.sendMessage("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c.");
                  } else if (!ptarget.getFriends().isFriend(profile.getPlayerName())) {
                    player.sendMessage("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + "§c não são amigos.");
                  } else {
                    profile.getFriends().removeFriend(ptarget.getPlayerName());
                    ptarget.getFriends().removeFriend(profile.getPlayerName());
                    profile.save();
                    ptarget.save();
                    player.sendMessage(" \n §cQue pena, agora você não é mais amigo de " + Role.getPrefixed(ptarget.getPlayerName()) + "\n §cEsperamos que um dia vocês possam se reconciliar.\n ");
                    if (!ptarget.isCurrentOnline()) {
                      ptarget.destroy();
                    } else {
                      ptarget.sendMessage(" \n §cQue pena, agora você não é mais amigo de " + Role.getPrefixed(profile.getPlayerName()) + " \n §cEsperamos que um dia vocês possam se reconciliar.\n ");
                    }
                  }

                  ptarget = null;
                }
              }
            } else if (action.equalsIgnoreCase("recusar")) {
              if (args.length < 2) {
                player.sendMessage("§cUtilize /amigo recusar [jogador]");
              } else {
                target = args[1];
                if (target.equalsIgnoreCase(player.getName())) {
                  player.sendMessage("§cVocê não pode recusar solicitações de você mesmo.");
                } else {
                  ptarget = Profile.loadIfExists(target);
                  if (ptarget == null) {
                    player.sendMessage("§cUsuário não encontrado.");
                  } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                    player.sendMessage("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §eestá na sua lista negra.");
                  } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                    player.sendMessage("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c.");
                  } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
                    player.sendMessage("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + " §cjá são amigos.");
                  } else if (!ptarget.getRequests().isRequested(profile.getPlayerName())) {
                    player.sendMessage("§cEste usuário não enviou nenhuma solicitação para você.");
                  } else {
                    ptarget.getRequests().removeRequest(profile.getPlayerName());
                    ptarget.save();
                    player.sendMessage(" \n §cVocê negou a solicitação de amizade de §7" + Role.getPrefixed(ptarget.getPlayerName()) + "§c.\n ");
                    if (!ptarget.isCurrentOnline()) {
                      ptarget.destroy();
                    } else {
                      ptarget.sendMessage(" \n §f" + Role.getPrefixed(profile.getPlayerName()) + " §cnegou sua solicitação de amizade.\n ");
                    }
                  }

                  ptarget = null;
                }
              }
            } else if (action.equalsIgnoreCase("bloquear")) {
              if (args.length < 2) {
                player.sendMessage("§cUtilize /amigo bloquear [jogador]");
              } else {
                target = args[1];
                ptarget = Profile.loadIfExists(target);
                if (target.equalsIgnoreCase(player.getName())) {
                  player.sendMessage("§cVocê não pode bloquear você mesmo.");
                } else if (ptarget == null) {
                  player.sendMessage("§cUsuário não encontrado.");
                } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                  player.sendMessage("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cjá está na sua lista negra.");
                } else {
                  profile.getBlackList().addToBlackList(ptarget.getPlayerName());
                  profile.getFriends().removeFriend(ptarget.getPlayerName());
                  profile.getRequests().removeRequest(ptarget.getPlayerName());
                  ptarget.getFriends().removeFriend(profile.getPlayerName());
                  ptarget.getRequests().removeRequest(profile.getPlayerName());
                  profile.save();
                  ptarget.save();
                  player.sendMessage(" \n §aVocê adicionou §f" + Role.getPrefixed(ptarget.getPlayerName()) + " §aa sua lista negra.\n ");
                  if (!ptarget.isCurrentOnline()) {
                    ptarget.destroy();
                  }
                }
              }
            } else if (action.equalsIgnoreCase("pedidos")) {
              page = 1;
              if (args.length > 1) {
                try {
                  page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var17) {
                }
              }

              if (page < 1) {
                player.sendMessage("§cUtilize um número válido.");
              } else {
                this.listRequests(player, profile, page);
              }
            } else if (action.equalsIgnoreCase("listanegra")) {
              page = 1;
              if (args.length > 1) {
                try {
                  page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var16) {
                }
              }

              if (page < 1) {
                player.sendMessage("§cUtilize um número válido.");
              } else {
                this.listBlackList(player, profile, page);
              }
            }
          }
        } else if (args.length < 2) {
          player.sendMessage("§cUtilize /amigo add [jogador]");
        } else {
          target = args[1];
          if (target.equalsIgnoreCase(player.getName())) {
            player.sendMessage("§cVocê não pode enviar solicitações para você mesmo.");
          } else {
            ptarget = Profile.loadIfExists(target);
            if (ptarget == null) {
              player.sendMessage("§cUsuário não encontrado.");
            } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
              player.sendMessage("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cestá na sua lista negra.");
            } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
              player.sendMessage("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c.");
            } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
              player.sendMessage("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + " §cjá são amigos.");
            } else if (profile.getRequests().isRequested(ptarget.getPlayerName())) {
              player.sendMessage("§cVocê já enviou uma solicitação de amizade para " + Role.getPrefixed(ptarget.getPlayerName()) + "§c.");
            } else if (ptarget.getRequests().isRequested(profile.getPlayerName())) {
              profile.getFriends().addFriend(ptarget.getPlayerName());
              ptarget.getFriends().addFriend(profile.getPlayerName());
              ptarget.getRequests().removeRequest(profile.getPlayerName());
              profile.save();
              ptarget.save();
              player.sendMessage(" \n §eO jogador" + Role.getPrefixed(ptarget.getPlayerName()) + "§eé agora seu amigo!\n ");
              if (!ptarget.isCurrentOnline()) {
                ptarget.destroy();
              } else {
                ptarget.sendMessage(" \n §f" + Role.getPrefixed(profile.getPlayerName()) + " §aaceitou sua solicitação de amizade!\n ");
              }
            } else {
              profile.getRequests().addRequest(ptarget.getPlayerName());
              profile.save();
              player.sendMessage(" \n §eVocê enviou uma solicitação de amizade para §f" + Role.getPrefixed(ptarget.getPlayerName()) + "§e.\n ");
              if (!ptarget.isCurrentOnline()) {
                ptarget.destroy();
              } else {
                List<BaseComponent> list = new ArrayList();
                BaseComponent[] var10 = TextComponent.fromLegacyText(" \n§eVocê recebeu uma solicitação de amizade de " + Role.getPrefixed(profile.getPlayerName()) + "\n");
                int var11 = var10.length;

                int var12;
                BaseComponent l;
                for(var12 = 0; var12 < var11; ++var12) {
                  l = var10[var12];
                  list.add(l);
                }

                var10 = TextComponent.fromLegacyText("§eClique ");
                var11 = var10.length;

                for(var12 = 0; var12 < var11; ++var12) {
                  l = var10[var12];
                  list.add(l);
                }

                TextComponent accept = new TextComponent("§a§lAQUI");
                accept.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para aceitar a solicitação de " + Role.getPrefixed(player.getName()) + ".")));
                accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo aceitar " + player.getName()));
                list.add(accept);
                list.add(new TextComponent(" §eou "));
                TextComponent reject = new TextComponent("§c§lAQUI");
                reject.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para recusar a solicitação de " + Role.getPrefixed(player.getName()) + ".")));
                reject.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo recusar " + player.getName()));
                list.add(reject);
                list.add(new TextComponent(" §epara negar solicitação de amizade.\n "));
                ptarget.sendMessage((BaseComponent[])((BaseComponent[])list.toArray(new BaseComponent[list.size()])));
              }
            }

            ptarget = null;
          }
        }
      }

      player = null;
      profile = null;
    }

  }

  private void listFriends(Player player, Profile profile, int page) {
    Map<Integer, List<BaseComponent>> pages = new HashMap();
    int index = 6;

    for(Iterator var6 = profile.getFriends().listFriends().iterator(); var6.hasNext(); ++index) {
      String friend = (String)var6.next();
      List<BaseComponent> list = (List)pages.get(index / 6);
      if (list == null) {
        list = new ArrayList();
        pages.put(index / 6, list);
      }

      BaseComponent[] var9 = TextComponent.fromLegacyText(Role.getPrefixed(friend) + " " + (Profile.isOnline(friend) ? "§eestá online." : "§7está offline.") + " §8- ");
      int var10 = var9.length;

      int var11;
      BaseComponent comp;
      for(var11 = 0; var11 < var10; ++var11) {
        comp = var9[var11];
        ((List)list).add(comp);
      }

      var9 = TextComponent.fromLegacyText("§6[Excluir] \n ");
      var10 = var9.length;

      for(var11 = 0; var11 < var10; ++var11) {
        comp = var9[var11];
        comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo excluir " + friend));
        comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para excluir " + Role.getPrefixed(friend) + "§7.")));
        ((List)list).add(comp);
      }
    }

    if (pages.isEmpty()) {
      player.sendMessage("§cSua lista de amizades está vazia.");
    } else if (!pages.containsKey(page)) {
      player.sendMessage("§cA página " + page + " não foi encontrada.");
    } else {
      player.sendMessage("");
      player.sendMessage("§eAmigos - " + page + "/" + index / 6);
      player.sendMessage("");
      player.spigot().sendMessage((BaseComponent[])((BaseComponent[])((List)pages.get(page)).toArray(new BaseComponent[((List)pages.get(page)).size()])));
    }

    pages.clear();
  }

  private void listRequests(Player player, Profile profile, int page) {
    Map<Integer, List<BaseComponent>> pages = new HashMap();
    int index = 6;
    CachedRowSet rs = Database.getInstance().getRequests(profile.getPlayerName());
    int var11;
    BaseComponent comp;
    if (rs != null) {
      try {
        rs.beforeFirst();

        while(rs.next()) {
          String request = rs.getString("name");
          List<BaseComponent> list = (List)pages.get(index / 6);
          if (list == null) {
            list = new ArrayList();
            pages.put(index / 6, list);
          }

          BaseComponent[] var9 = TextComponent.fromLegacyText(Role.getPrefixed(request) + " §7- ");
          int var10 = var9.length;

          for(var11 = 0; var11 < var10; ++var11) {
            comp = var9[var11];
            ((List)list).add(comp);
          }

          var9 = TextComponent.fromLegacyText("§6[Aceitar] §7ou ");
          var10 = var9.length;

          for(var11 = 0; var11 < var10; ++var11) {
            comp = var9[var11];
            comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo aceitar " + request));
            comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para aceitar a solicitação de " + Role.getPrefixed(request) + "§7.")));
            ((List)list).add(comp);
          }

          var9 = TextComponent.fromLegacyText("§6[Recusar] \n ");
          var10 = var9.length;

          for(var11 = 0; var11 < var10; ++var11) {
            comp = var9[var11];
            comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo recusar " + request));
            comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para recusar a solicitação de " + Role.getPrefixed(request) + "§7.")));
            ((List)list).add(comp);
          }

          ++index;
        }
      } catch (SQLException var14) {
      }

      rs = null;
    }

    for(Iterator var15 = profile.getRequests().listRequests().iterator(); var15.hasNext(); ++index) {
      String request = (String)var15.next();
      List<BaseComponent> list = (List)pages.get(index / 6);
      if (list == null) {
        list = new ArrayList();
        pages.put(index / 6, list);
      }

      BaseComponent[] var18 = TextComponent.fromLegacyText(Role.getPrefixed(request) + " §8- ");
      var11 = var18.length;

      int var19;
      for(var19 = 0; var19 < var11; ++var19) {
        comp = var18[var19];
        ((List)list).add(comp);
      }

      var18 = TextComponent.fromLegacyText("§6[Cancelar] \n ");
      var11 = var18.length;

      for(var19 = 0; var19 < var11; ++var19) {
        comp = var18[var19];
        comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo cancelar " + request));
        comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para cancelar a solicitação para " + Role.getPrefixed(request) + "§7.")));
        ((List) list).add(comp);
      }
    }

    if (pages.isEmpty()) {
      player.sendMessage("§cVocê não possui nenhuma solicitação de amizade pendente.");
    } else if (!pages.containsKey(page)) {
      player.sendMessage("§cA página " + page + " não foi encontrada.");
    } else {
      player.sendMessage("");
      player.sendMessage("§eSolicitações - " + page + "/" + index / 6);
      player.sendMessage("");
      player.spigot().sendMessage((BaseComponent[])((BaseComponent[])((List)pages.get(page)).toArray(new BaseComponent[((List)pages.get(page)).size()])));
    }

    pages.clear();
  }

  private void listBlackList(Player player, Profile profile, int page) {
    Map<Integer, List<BaseComponent>> pages = new HashMap();
    int index = 6;

    for(Iterator var6 = profile.getBlackList().listBlackListed().iterator(); var6.hasNext(); ++index) {
      String blacklist = (String)var6.next();
      List<BaseComponent> list = (List)pages.get(index / 6);
      if (list == null) {
        list = new ArrayList();
        pages.put(index / 6, list);
      }

      BaseComponent[] var9 = TextComponent.fromLegacyText(Role.getPrefixed(blacklist) + " ");
      int var10 = var9.length;

      int var11;
      BaseComponent comp;
      for(var11 = 0; var11 < var10; ++var11) {
        comp = var9[var11];
        ((List)list).add(comp);
      }

      var9 = TextComponent.fromLegacyText("§aDesbloquear\n");
      var10 = var9.length;

      for(var11 = 0; var11 < var10; ++var11) {
        comp = var9[var11];
        comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo desbloquear " + blacklist));
        comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para desbloquear " + Role.getPrefixed(blacklist) + "§7.")));
        ((List)list).add(comp);
      }
    }

    if (pages.isEmpty()) {
      player.sendMessage("§cVocê não possui nenhum usuário na lista negra.");
    } else if (!pages.containsKey(page)) {
      player.sendMessage("§cA página " + page + " não foi encontrada.");
    } else {
      player.sendMessage("");
      player.sendMessage("§eLista Negra - " + page + "/" + index / 6);
      player.sendMessage("");
      player.spigot().sendMessage((BaseComponent[])((BaseComponent[])((List)pages.get(page)).toArray(new BaseComponent[((List)pages.get(page)).size()])));
    }

    pages.clear();
  }

  private void sendHelp(Player player) {
    player.sendMessage(" \n§6/amigo aceitar [jogador] §f- §7Aceitar uma solicitação de amizade.\n§6/amigo add [jogador] §f- §7Enviar uma solicitação de amizade.\n§6/amigo ajuda §f- §7Mostra essa mensagem de ajuda.\n§6/amigo cancelar [jogador] §f- §7Cancelar uma solicitação de amizade.\n§6/amigo party §f- §7Convidar todos os seus amigos para uma party.\n§6/amigo listar [página] §f- §7Listar suas amizades.\n§6/amigo excluir [jogador] §f- §7Desfazer uma amizade.\n§6/amigo recusar [jogador] §f- §7Negar uma solicitação de amizade.\n§6/amigo pedidos [página] §f- §7Listar suas solicitações.\n ");
  }
}
