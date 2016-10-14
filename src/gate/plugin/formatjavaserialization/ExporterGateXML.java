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
import gate.DocumentExporter;
import gate.FeatureMap;
import gate.corpora.DocumentStaxUtils;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;
import org.xerial.snappy.SnappyOutputStream;

@SuppressWarnings("serial")
@CreoleResource(
        name = "Gate XML Exporter", 
        tool = true, 
        autoinstances = @AutoInstance, 
        comment = "Export GATE documents as GATE XML", 
        helpURL = ""
)
public class ExporterGateXML extends DocumentExporter {

  public ExporterGateXML() {
    super("GATE XML","gatexml","application/xml+gate");
  }
  
  public void export(Document doc, OutputStream out, FeatureMap options)
    throws IOException {
    try  {
      DocumentStaxUtils.writeDocument(doc, out, "");
    } catch(Exception e) {
      throw new IOException(e);
    }
  }
}
