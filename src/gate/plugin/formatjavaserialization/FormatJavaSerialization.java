/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-Format_JavaSerialization
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package gate.plugin.formatjavaserialization;

import gate.Document;
import gate.DocumentFormat;
import gate.Resource;
import gate.corpora.DocumentImpl;
import gate.corpora.MimeType;
import gate.corpora.RepositioningInfo;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.util.DocumentFormatException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Set;


@CreoleResource(
        name = "Java Object Serialization Format", 
        isPrivate = true, 
        autoinstances = {@AutoInstance(hidden = true)}, 
        comment = "Support for reading documents serialized as Java objects", 
        helpURL = "")
public class FormatJavaSerialization extends DocumentFormat  {

  //private static final long serialVersionUID = -2394353168913311584L;
  
  /** Default construction */
  public FormatJavaSerialization() {
    super();
  }

  /** We could collect repositioning information during XML parsing */
  @Override
  public Boolean supportsRepositioning() {
    return new Boolean(false);
  }

  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException {
    MimeType mime = new MimeType("application", "javaserialization");
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(),this);
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    suffixes2mimeTypeMap.put("ser", mime);
    setMimeType(mime);
    return this;
  }

  @Override
  public void unpackMarkup(Document dcmnt) throws DocumentFormatException {
    // For this document format, we are really only interested in the 
    // source URL at this point
    // It does not make sense to proceed without a source URL so if there
    // is none, we complain.
    URL sourceURL = dcmnt.getSourceUrl();
    if(sourceURL == null) {
      throw new DocumentFormatException("Cannot create document, no sourceURL");
    }
    Object obj = null;
    try (
            InputStream urlStream = sourceURL.openStream();
            ObjectInputStream oistream =
                    new ObjectInputStream(urlStream);
            ) {
      obj = oistream.readObject();
    } catch (Exception ex) {
      throw new DocumentFormatException("Exception when trying to read the document "+sourceURL,ex);
    }
    // There is a problem here: the document could in theory be any implementation
    // of document, but we already have the default implementation from the 
    // constructor of DocumentImpl from where we get called. For now we just
    // make sure that we get DocumentImpl as well.
    if(obj instanceof DocumentImpl) {
      DocumentImpl doc = (DocumentImpl)obj;
      // copy over all the content into the empty document created by the factory
      // but do not copy the sourceURL!
      dcmnt.setContent(doc.getContent());
      dcmnt.getFeatures().putAll(doc.getFeatures());
      // this will have overriden the sourceURL feature, so restore it
      dcmnt.getFeatures().put("gate.SourceURL",sourceURL);
      // TODO: not sure if there is something we should or can do about the 
      // remaining document init parameters
      
      // copy over the default annotation set.
      dcmnt.getAnnotations().addAll(doc.getAnnotations());
      // finally copy over all the named annotation sets
      Set<String> setNames = doc.getAnnotationSetNames();
      if(setNames != null) {
        for(String setName : setNames) {
          dcmnt.getAnnotations(setName).addAll(doc.getAnnotations(setName));
        }
      }
    }
  }

  @Override
  public void unpackMarkup(Document dcmnt, RepositioningInfo ri, RepositioningInfo ri1) throws DocumentFormatException {
    throw new UnsupportedOperationException("We should not get here, not supported"); 
  }
}
