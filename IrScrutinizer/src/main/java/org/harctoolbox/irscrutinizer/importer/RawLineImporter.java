/*
Copyright (C) 2013, 2014 Bengt Martensson.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or (at
your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program. If not, see http://www.gnu.org/licenses/.
*/

package org.harctoolbox.irscrutinizer.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import org.harctoolbox.IrpMaster.IrSignal;
import org.harctoolbox.IrpMaster.IrpMasterException;
import org.harctoolbox.girr.Command;
import org.harctoolbox.harchardware.ir.InterpretStringHardware;

/**
 * This class does something interesting and useful. Or not...
 */
public class RawLineImporter extends RemoteSetImporter implements IReaderImporter {

    public static void main(String[] args) {
        RawLineImporter rli = new RawLineImporter();
        try {
            rli.load(new File(args[0]), "WINDOWS-1252");
            for (Command command : rli.getCommands())
                System.out.println(command.toPrintString());
        } catch (IOException | IrpMasterException | java.text.ParseException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public String[][] getFileExtensions() {
        return new String[][] { new String[]{ "Text files (*.txt *.text)", "txt", "text" }};
    }

    @Override
    public void load(Reader reader, String origin) throws IOException {
        prepareLoad(origin);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String name = null;
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null)
                break;

            if (line.trim().isEmpty())
                continue;

            IrSignal irSignal = null;
            try {
                irSignal = InterpretStringHardware.interpretString(line, getFallbackFrequency(), isInvokeRepeatFinder(), isInvokeAnalyzer());
            } catch (IrpMasterException ex) {
                name = line;
            }

            if (name != null && irSignal != null) {
                Command command = new Command(uniqueName(name), null /*comment*/, irSignal);
                addCommand(command);
                name = null;
            }
        }
        setupRemoteSet();
    }

    @Override
    public String getFormatName() {
        return "Text file";
    }
}
