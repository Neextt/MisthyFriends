package net.lxsthw.friends.bungee.cmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;

import net.lxsthw.friends.database.Database;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.lxsthw.friends.profile.Profile;
import tk.slicecollections.maxteer.player.role.Role;

public class FriendsCommand extends Commands {
  public FriendsCommand() {
    super("amigo", new String[]{"friend"});
  }

  public void execute(CommandSender sender, String[] args) {
    if (sender instanceof ProxiedPlayer) {
      ProxiedPlayer player = (ProxiedPlayer)sender;
      Profile profile = Profile.createOrLoadProfile(player.getName());
      if (args.length == 0) {
        this.sendHelp(player);
      } else {
        String action = args[0];
        String target;
        Profile ptarget;
        if (action.equalsIgnoreCase("aceitar")) {
          if (args.length < 2) {
            player.sendMessage(TextComponent.fromLegacyText("§cUtilize /amigo aceitar [jogador]"));
          } else {
            target = args[1];
            if (target.equalsIgnoreCase(player.getName())) {
              player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode aceitar convites de você mesmo."));
            } else {
              ptarget = Profile.loadIfExists(target);
              if (ptarget == null) {
                player.sendMessage(TextComponent.fromLegacyText("§cUsuário não encontrado."));
              } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                player.sendMessage(TextComponent.fromLegacyText("§c" + Role.getPrefixed(ptarget.getPlayerName()) + "§c está na sua lista negra."));
              } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c."));
              } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + " §cjá são amigos."));
              } else if (!ptarget.getRequests().isRequested(profile.getPlayerName())) {
                player.sendMessage(TextComponent.fromLegacyText("§cEste usuário não enviou nenhuma solicitação para você."));
              } else {
                profile.getFriends().addFriend(ptarget.getPlayerName());
                ptarget.getFriends().addFriend(profile.getPlayerName());
                ptarget.getRequests().removeRequest(profile.getPlayerName());
                profile.save();
                ptarget.save();
                player.sendMessage(TextComponent.fromLegacyText("\n§eO jogador §f" + Role.getPrefixed(ptarget.getPlayerName()) + "§e é agora seu amigo!\n "));
                if (!ptarget.isCurrentOnline()) {
                  ptarget.destroy();
                } else {
                  ptarget.sendMessage(TextComponent.fromLegacyText(" \n §7" + Role.getPrefixed(profile.getPlayerName()) + " §aaceitou o sua solicitação de amizade.\n "));
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
              player.sendMessage(TextComponent.fromLegacyText("§cUtilize /amigo cancelar [jogador]"));
            } else {
              target = args[1];
              if (target.equalsIgnoreCase(player.getName())) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode cancelar solicitações para você mesmo."));
              } else {
                ptarget = Profile.loadIfExists(target);
                if (ptarget == null) {
                  player.sendMessage(TextComponent.fromLegacyText("§cUsuário não encontrado."));
                } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cestá na sua lista negra."));
                } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c."));
                } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + "§c já são amigos."));
                } else if (!profile.getRequests().isRequested(ptarget.getPlayerName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§cVocê não enviou nenhuma solicitação de amizade para " + Role.getPrefixed(ptarget.getPlayerName()) + "§c."));
                } else {
                  profile.getRequests().removeRequest(ptarget.getPlayerName());
                  profile.save();
                  player.sendMessage(TextComponent.fromLegacyText(" \n §cVocê cancelou sua solicitação de amizade para §7" + Role.getPrefixed(ptarget.getPlayerName()) + "§a.\n "));
                  if (!ptarget.isCurrentOnline()) {
                    ptarget.destroy();
                  } else {
                    ptarget.sendMessage(TextComponent.fromLegacyText(" \n O jogador §7" + Role.getPrefixed(profile.getPlayerName()) + " §crecusou sua solicitação de amizade.\n "));
                  }
                }

                ptarget = null;
              }
            }
          } else if (action.equalsIgnoreCase("desbloquear")) {
            if (args.length < 2) {
              player.sendMessage(TextComponent.fromLegacyText("§cUtilize /amigo desbloquear [jogador]"));
            } else {
              target = args[1];
              ptarget = Profile.loadIfExists(target);
              if (target.equalsIgnoreCase(player.getName())) {
                player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode desbloquear você mesmo."));
              } else if (ptarget == null) {
                player.sendMessage(TextComponent.fromLegacyText("§cUsuário não encontrado."));
              } else if (!profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                player.sendMessage(TextComponent.fromLegacyText("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cnão está na sua lista negra."));
              } else {
                profile.getBlackList().removeFromBlackList(ptarget.getPlayerName());
                profile.save();
                player.sendMessage(TextComponent.fromLegacyText(" \n §aVocê removeu §7" + Role.getPrefixed(ptarget.getPlayerName()) + " §ada sua lista negra.\n "));
                if (!ptarget.isCurrentOnline()) {
                  ptarget.destroy();
                }
              }
            }
          } else {
            int page;
            if (action.equalsIgnoreCase("list")) {
              page = 1;
              if (args.length > 1) {
                try {
                  page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var15) {
                }
              }

              if (page < 1) {
                player.sendMessage(TextComponent.fromLegacyText("§cUtilize um número válido."));
              } else {
                this.listFriends(player, profile, page);
              }
            } else if (action.equalsIgnoreCase("excluir")) {
              if (args.length < 2) {
                player.sendMessage(TextComponent.fromLegacyText("§cUtilize /amigo excluir [jogador]"));
              } else {
                target = args[1];
                if (target.equalsIgnoreCase(player.getName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode excluir você mesmo."));
                } else {
                  ptarget = Profile.loadIfExists(target);
                  if (ptarget == null) {
                    player.sendMessage(TextComponent.fromLegacyText("§cUsuário não encontrado."));
                  } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                    player.sendMessage(TextComponent.fromLegacyText("§c" + Role.getPrefixed(ptarget.getPlayerName()) + " §cestá na sua lista negra."));
                  } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                    player.sendMessage(TextComponent.fromLegacyText("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c."));
                  } else if (!ptarget.getFriends().isFriend(profile.getPlayerName())) {
                    player.sendMessage(TextComponent.fromLegacyText("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + "§c não são amigos."));
                  } else {
                    profile.getFriends().removeFriend(ptarget.getPlayerName());
                    ptarget.getFriends().removeFriend(profile.getPlayerName());
                    profile.save();
                    ptarget.save();
                    player.sendMessage(TextComponent.fromLegacyText("\n §cQue pena, agora você não é mais amigo de " + Role.getPrefixed(ptarget.getPlayerName()) + "\n §cEsperamos que um dia vocês possam se reconciliar.\n "));
                    if (!ptarget.isCurrentOnline()) {
                      ptarget.destroy();
                    } else {
                      ptarget.sendMessage(TextComponent.fromLegacyText(" \n §cQue pena, agora você não é mais amigo de " + Role.getPrefixed(ptarget.getPlayerName()) + "\n §cEsperamos que um dia vocês possam se reconciliar.\n "));
                    }
                  }

                  ptarget = null;
                }
              }
            } else if (action.equalsIgnoreCase("recusar")) {
              if (args.length < 2) {
                player.sendMessage(TextComponent.fromLegacyText("§cUtilize /amigo recusar <jogador>"));
              } else {
                target = args[1];
                if (target.equalsIgnoreCase(player.getName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode recusar solicitações de você mesmo."));
                } else {
                  ptarget = Profile.loadIfExists(target);
                  if (ptarget == null) {
                    player.sendMessage(TextComponent.fromLegacyText("§cUsuário não encontrado."));
                  } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                    player.sendMessage(TextComponent.fromLegacyText("§c" + Role.getPrefixed(ptarget.getPlayerName()) + "§c está na sua lista negra."));
                  } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
                    player.sendMessage(TextComponent.fromLegacyText("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c."));
                  } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
                    player.sendMessage(TextComponent.fromLegacyText("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + " §cjá são amigos."));
                  } else if (!ptarget.getRequests().isRequested(profile.getPlayerName())) {
                    player.sendMessage(TextComponent.fromLegacyText("§cEste usuário não enviou nenhuma solicitação para você."));
                  } else {
                    ptarget.getRequests().removeRequest(profile.getPlayerName());
                    ptarget.save();
                    player.sendMessage(TextComponent.fromLegacyText(" \n §cVocê recusou a solicitação de amizade de §f" + Role.getPrefixed(ptarget.getPlayerName()) + "§c.\n "));
                    if (!ptarget.isCurrentOnline()) {
                      ptarget.destroy();
                    } else {
                      ptarget.sendMessage(TextComponent.fromLegacyText(" \n §f" + Role.getPrefixed(profile.getPlayerName()) + " §crecusou sua solicitação de amizade.\n "));
                    }
                  }

                  ptarget = null;
                }
              }
            } else if (action.equalsIgnoreCase("bloquear")) {
              if (args.length < 2) {
                player.sendMessage(TextComponent.fromLegacyText("§cUtilize /amigo bloquear [jogador]"));
              } else {
                target = args[1];
                ptarget = Profile.loadIfExists(target);
                if (target.equalsIgnoreCase(player.getName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode bloquear você mesmo."));
                } else if (ptarget == null) {
                  player.sendMessage(TextComponent.fromLegacyText("§cUsuário não encontrado."));
                } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
                  player.sendMessage(TextComponent.fromLegacyText("§c" + Role.getPrefixed(ptarget.getPlayerName()) + "§c já está na sua lista negra."));
                } else {
                  profile.getBlackList().addToBlackList(ptarget.getPlayerName());
                  profile.getFriends().removeFriend(ptarget.getPlayerName());
                  profile.getRequests().removeRequest(ptarget.getPlayerName());
                  ptarget.getFriends().removeFriend(profile.getPlayerName());
                  ptarget.getRequests().removeRequest(profile.getPlayerName());
                  profile.save();
                  ptarget.save();
                  player.sendMessage(TextComponent.fromLegacyText(" \n §aVocê adicionou §f" + Role.getPrefixed(ptarget.getPlayerName()) + " §aa sua lista negra.\n "));
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
                } catch (NumberFormatException var14) {
                }
              }

              if (page < 1) {
                player.sendMessage(TextComponent.fromLegacyText("§cUtilize um número válido."));
              } else {
                this.listRequests(player, profile, page);
              }
            } else if (action.equalsIgnoreCase("listanegra")) {
              page = 1;
              if (args.length > 1) {
                try {
                  page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var13) {
                }
              }

              if (page < 1) {
                player.sendMessage(TextComponent.fromLegacyText("§cUtilize um número válido."));
              } else {
                this.listBlackList(player, profile, page);
              }
            }
          }
        } else if (args.length < 2) {
          player.sendMessage(TextComponent.fromLegacyText("§cUtilize /amigo add [jogador]"));
        } else {
          target = args[1];
          if (target.equalsIgnoreCase(player.getName())) {
            player.sendMessage(TextComponent.fromLegacyText("§cVocê não pode enviar solicitações para você mesmo."));
          } else {
            ptarget = Profile.loadIfExists(target);
            if (ptarget == null) {
              player.sendMessage(TextComponent.fromLegacyText("§cUsuário não encontrado."));
            } else if (profile.getBlackList().isInBlackList(ptarget.getPlayerName())) {
              player.sendMessage(TextComponent.fromLegacyText("§c" + Role.getPrefixed(ptarget.getPlayerName()) + "§c está na sua lista negra."));
            } else if (ptarget.getBlackList().isInBlackList(profile.getPlayerName())) {
              player.sendMessage(TextComponent.fromLegacyText("§cVocê está na lista negra de " + Role.getPrefixed(ptarget.getPlayerName()) + "§c."));
            } else if (ptarget.getFriends().isFriend(profile.getPlayerName())) {
              player.sendMessage(TextComponent.fromLegacyText("§cVocê e " + Role.getPrefixed(ptarget.getPlayerName()) + " §cjá são amigos."));
            } else if (profile.getRequests().isRequested(ptarget.getPlayerName())) {
              player.sendMessage(TextComponent.fromLegacyText("§cVocê já enviou uma solicitação de amizade para " + Role.getPrefixed(ptarget.getPlayerName()) + "§c."));
            } else if (ptarget.getRequests().isRequested(profile.getPlayerName())) {
              profile.getFriends().addFriend(ptarget.getPlayerName());
              ptarget.getFriends().addFriend(profile.getPlayerName());
              ptarget.getRequests().removeRequest(profile.getPlayerName());
              profile.save();
              ptarget.save();
              player.sendMessage(TextComponent.fromLegacyText(" \n §aVocê aceitou o pedido de amizade de §7" + Role.getPrefixed(ptarget.getPlayerName()) + "§a.\n "));
              if (!ptarget.isCurrentOnline()) {
                ptarget.destroy();
              } else {
                ptarget.sendMessage(TextComponent.fromLegacyText(" \n §7" + Role.getPrefixed(profile.getPlayerName()) + " §aaceitou o seu pedido de amizade.\n "));
              }
            } else {
              profile.getRequests().addRequest(ptarget.getPlayerName());
              profile.save();
              player.sendMessage(TextComponent.fromLegacyText(" \n §eVocê enviou uma solicitação de amizade para §7" + Role.getPrefixed(ptarget.getPlayerName()) + "\n "));
              if (!ptarget.isCurrentOnline()) {
                ptarget.destroy();
              } else {
                List<BaseComponent> list = new ArrayList();
                BaseComponent[] var9 = TextComponent.fromLegacyText(" \n§eVocê recebeu uma solicitação de amizade de " + Role.getPrefixed(profile.getPlayerName()) + "\n");
                int var10 = var9.length;

                int var11;
                BaseComponent l;
                for(var11 = 0; var11 < var10; ++var11) {
                  l = var9[var11];
                  list.add(l);
                }

                var9 = TextComponent.fromLegacyText("§eClique ");
                var10 = var9.length;

                for(var11 = 0; var11 < var10; ++var11) {
                  l = var9[var11];
                  list.add(l);
                }

                TextComponent accept = new TextComponent("§a§lAQUI");
                accept.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para aceitar a solicitação de " + Role.getPrefixed(player.getName()) + "§7.")));
                accept.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo aceitar " + player.getName()));
                list.add(accept);
                list.add(new TextComponent(" §eou "));
                TextComponent reject = new TextComponent("§c§lAQUI");
                reject.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para recusar a solicitação de " + Role.getPrefixed(player.getName()) + "§7.")));
                reject.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo recusar " + player.getName()));
                list.add(reject);
                list.add(new TextComponent(" §epara negar a solicitação de amizade.\n "));
                ptarget.sendMessage((BaseComponent[])list.toArray(new BaseComponent[list.size()]));
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

  private void listFriends(ProxiedPlayer player, Profile profile, int page) {
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
        comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para excluir " + Role.getPrefixed(friend) + ".")));
        ((List)list).add(comp);
      }
    }

    if (pages.isEmpty()) {
      player.sendMessage(TextComponent.fromLegacyText("§cSua lista de amizades está vazia."));
    } else if (!pages.containsKey(page)) {
      player.sendMessage(TextComponent.fromLegacyText("§cA página " + page + " não foi encontrada."));
    } else {
      player.sendMessage(TextComponent.fromLegacyText(""));
      player.sendMessage(TextComponent.fromLegacyText("§eAmigos - " + page + "/" + index / 6));
      player.sendMessage(TextComponent.fromLegacyText(""));
      player.sendMessage((BaseComponent[])((List)pages.get(page)).toArray(new BaseComponent[((List)pages.get(page)).size()]));
    }

    pages.clear();
  }

  private void listRequests(ProxiedPlayer player, Profile profile, int page) {
    Map<Integer, List<BaseComponent>> pages = new HashMap();
    int index = 6;
    CachedRowSet rs = Database.getInstance().getRequests(profile.getPlayerName());
    int var11;
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

          BaseComponent[] var9 = TextComponent.fromLegacyText(Role.getPrefixed(request) + " §8- ");
          int var10 = var9.length;

          BaseComponent comp;
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

      BaseComponent comp;
      int var19;
      for(var19 = 0; var19 < var11; ++var19) {
        comp = var18[var19];
        ((List)list).add(comp);
      }

      var18 = TextComponent.fromLegacyText("§6[Cancelar]\n ");
      var11 = var18.length;

      for(var19 = 0; var19 < var11; ++var19) {
        comp = var18[var19];
        comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo cancelar " + request));
        comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para cancelar a solicitação para " + Role.getPrefixed(request) + "§7.")));
        ((List)list).add(comp);
      }
    }

    if (pages.isEmpty()) {
      player.sendMessage(TextComponent.fromLegacyText("§cVocê não possui nenhuma solicitação de amizade pendente."));
    } else if (!pages.containsKey(page)) {
      player.sendMessage(TextComponent.fromLegacyText("§cA página " + page + " não foi encontrada."));
    } else {
      player.sendMessage(TextComponent.fromLegacyText(""));
      player.sendMessage(TextComponent.fromLegacyText("§eSolicitações - " + page + "/" + index / 6));
      player.sendMessage(TextComponent.fromLegacyText(""));
      player.sendMessage((BaseComponent[])((List)pages.get(page)).toArray(new BaseComponent[((List)pages.get(page)).size()]));
    }

    pages.clear();
  }

  private void listBlackList(ProxiedPlayer player, Profile profile, int page) {
    Map<Integer, List<BaseComponent>> pages = new HashMap();
    int index = 6;

    for(Iterator var6 = profile.getBlackList().listBlackListed().iterator(); var6.hasNext(); ++index) {
      String blacklist = (String)var6.next();
      List<BaseComponent> list = (List)pages.get(index / 6);
      if (list == null) {
        list = new ArrayList();
        pages.put(index / 6, list);
      }

      BaseComponent[] var9 = TextComponent.fromLegacyText(Role.getPrefixed(blacklist) + " §7- ");
      int var10 = var9.length;

      int var11;
      BaseComponent comp;
      for(var11 = 0; var11 < var10; ++var11) {
        comp = var9[var11];
        ((List)list).add(comp);
      }

      var9 = TextComponent.fromLegacyText("§a[Desbloquear]\n");
      var10 = var9.length;

      for(var11 = 0; var11 < var10; ++var11) {
        comp = var9[var11];
        comp.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/amigo desbloquear " + blacklist));
        comp.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Clique aqui para desbloquear " + Role.getPrefixed(blacklist) + "§7.")));
        ((List)list).add(comp);
      }
    }

    if (pages.isEmpty()) {
      player.sendMessage(TextComponent.fromLegacyText("§cVocê não possui nenhum usuário na lista negra."));
    } else if (!pages.containsKey(page)) {
      player.sendMessage(TextComponent.fromLegacyText("§cA página " + page + " não foi encontrada."));
    } else {
      player.sendMessage(TextComponent.fromLegacyText(""));
      player.sendMessage(TextComponent.fromLegacyText("§eLista Negra - " + page + "/" + index / 6));
      player.sendMessage(TextComponent.fromLegacyText(""));
      player.sendMessage((BaseComponent[])((List)pages.get(page)).toArray(new BaseComponent[((List)pages.get(page)).size()]));
    }

    pages.clear();
  }

  private void sendHelp(ProxiedPlayer player) {
    player.sendMessage(TextComponent.fromLegacyText(" \n§6/amigo aceitar (jogador) §f- §7Aceitar uma solicitação de amizade.\n§6/amigo add (jogador) §f- §7Enviar uma solicitação de amizade.\n§6/amigo ajuda §f- §7Mostra essa mensagem de ajuda.\n§6/amigo cancelar (jogador) §f- §7Cancelar uma solicitação de amizade.\n§6/amigo party §f- §7Convidar todos os seus amigos para uma party.\n§6/amigo list (página) §f- §7Listar suas amizades.\n§6/amigo excluir (jogador) §f- §7Desfazer uma amizade.\n§6/amigo recusar (jogador) §f- §7Negar uma solicitação de amizade.\n§6/amigo pedidos §f- §7Listar suas solicitações.\n "));
  }
}
