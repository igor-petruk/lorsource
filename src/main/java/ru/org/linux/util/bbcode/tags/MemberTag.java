/*
 * Copyright (c) 2005-2006, Luke Plant
 * All rights reserved.
 * E-mail: <L.Plant.98@cantab.net>
 * Web: http://lukeplant.me.uk/
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *      * Redistributions in binary form must reproduce the above
 *        copyright notice, this list of conditions and the following
 *        disclaimer in the documentation and/or other materials provided
 *        with the distribution.
 *
 *      * The name of Luke Plant may not be used to endorse or promote
 *        products derived from this software without specific prior
 *        written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Rewrite with Java language and modified for lorsource by Ildar Hizbulin 2011
 * E-mail: <hizel@vyborg.ru>
 */

package ru.org.linux.util.bbcode.tags;

import ru.org.linux.user.User;
import ru.org.linux.user.UserDao;
import ru.org.linux.util.bbcode.Parser;
import ru.org.linux.util.bbcode.ParserParameters;
import ru.org.linux.util.bbcode.nodes.Node;
import ru.org.linux.util.bbcode.nodes.RootNode;
import ru.org.linux.util.bbcode.nodes.TagNode;
import ru.org.linux.util.bbcode.nodes.TextNode;
import ru.org.linux.util.formatter.ToHtmlFormatter;

import java.util.Set;

/**
 */
public class MemberTag extends Tag {
  public MemberTag(String name, Set<String> allowedChildren, String implicitTag, ParserParameters parserParameters) {
    super(name, allowedChildren, implicitTag, parserParameters);
  }

  @Override
  public String renderNodeXhtml(Node node) {
    if (node.lengthChildren() == 0) {
      return "";
    }
    TextNode txtNode = (TextNode) node.getChildren().iterator().next();
    String memberName = Parser.escape(txtNode.getText()).trim();
    String result;
    TagNode tagNode = (TagNode)node;
    RootNode rootNode = tagNode.getRootNode();
    ToHtmlFormatter toHtmlFormatter = rootNode.getToHtmlFormatter();
    boolean secure = rootNode.isSecure();
    UserDao userDao = rootNode.getUserDao();
    try {
      if(userDao != null && toHtmlFormatter != null){
        User user = rootNode.getUserDao().getUser(memberName);
        if (!user.isBlocked()) {
          result = String.format("<span style=\"white-space: nowrap\"><img src=\"/img/tuxlor.png\"><a style=\"text-decoration: none\" href=\"%s\">%s</a></span>",
              toHtmlFormatter.memberURL(user, secure), Parser.escape(memberName));
          rootNode.addReplier(user);
        } else {
          result = String.format("<span style=\"white-space: nowrap\"><img src=\"/img/tuxlor.png\"><s><a style=\"text-decoration: none\" href=\"%s\">%s</a></s></span>",
              toHtmlFormatter.memberURL(user, secure), Parser.escape(memberName));
        }
      }else{
        result = Parser.escape(memberName);
      }
    } catch (Exception ex) {
      result = String.format("<s>%s</s>", Parser.escape(memberName));
    }
    return result;
  }
}
