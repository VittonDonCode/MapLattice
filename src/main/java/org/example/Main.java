package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.IntTag;
import java.util.Scanner;
import javax.swing.text.Position;
import java.util.ArrayList;
import net.querz.nbt.io.NBTSerializer;
import java.util.zip.GZIPOutputStream;
import java.io.FileOutputStream;

class BlockInfo //Individual block info, used to forming chains
{
    Boolean downTrueFlatFalse; //Keep as boolean as up blocks will be null
} //Up and down is relative moving north use "u, d, or f" lowercase in class

class BlockDebugger { //Class for debugging blocks mostly

    int id;

    CompoundTag tag;

    Position pos;

}

public class Main {

    public static int getY(ListTag<?> blocks, int index)
    {
        CompoundTag block = (CompoundTag) blocks.get(index);
        ListTag<?> pos = block.getListTag("pos");
        IntTag yTag = (IntTag) pos.get(1);
        return yTag.asInt();
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        NBTDeserializer deserializer = new NBTDeserializer();

        System.out.println("Hello and welcome!");

        try {
            System.out.print("NBT file name: ");
            String name = input.nextLine();
            Path file = Path.of("schematics/" + name + ".nbt");
            System.out.println(file.toAbsolutePath());
            var taggedMap = deserializer.fromFile(file.toFile());
            CompoundTag root = (CompoundTag) taggedMap.getTag();
            ListTag<?> blocks = root.getListTag("blocks");
            System.out.println(blocks.size());
            //System.out.println(blocks.get(0));

            int nextY = 0;
            int newY;
            int lastBlockYPos = 0;
            int thisBlockYPos = 0;
            Path output = Path.of("schematics/" + name + "_edited.nbt");
            ArrayList<BlockInfo> allBlocks = new ArrayList<>();

            for (int i = 0; i < blocks.size(); i++) //Upward Placer
            {
                BlockInfo info = new BlockInfo();

                if (i < blocks.size() - 1)
                {
                    nextY = getY(blocks, i + 1);
                }                lastBlockYPos = thisBlockYPos;
                CompoundTag block = (CompoundTag) blocks.get(i);
                ListTag<IntTag> pos = block.getListTag("pos").asIntTagList();                IntTag yTag = (IntTag) pos.get(1);
                int y = yTag.asInt();
                thisBlockYPos =  y;

                if (i % 129 == 0) //first row shading needs placeholders
                {

                    if (i < blocks.size() - 1)
                    {
                        nextY = getY(blocks, i + 1);
                    }
                    lastBlockYPos = thisBlockYPos;

                    if (nextY > y)
                    {
                        newY = i % 129;
                        pos.set(1, new IntTag(newY));
                        System.out.println("Block " + i + " is now " + newY);
                        info.downTrueFlatFalse = null;
                        allBlocks.add(info);
                    }
                    else if (nextY < y)
                    {
                        info.downTrueFlatFalse = true;
                        allBlocks.add(info);
                    }
                    else
                    {
                        info.downTrueFlatFalse = false;
                        allBlocks.add(info);
                    }
                } else
                {
                    if (thisBlockYPos > lastBlockYPos)
                    {
                        newY = i % 129;
                        pos.set(1, new IntTag(newY));
                        System.out.println("Block " + i + " is now " + newY);

                        allBlocks.add(info);
                    }
                    else if (thisBlockYPos == (lastBlockYPos))
                    {
                        info.downTrueFlatFalse = false;
                        allBlocks.add(info);
                    }
                    else
                    {
                        info.downTrueFlatFalse = true;
                        allBlocks.add(info);
                    }
                }
            }

            for (int i = blocks.size() - 1; i >= 0; i--) //Down and flat placer
            {
                CompoundTag block = (CompoundTag) blocks.get(i);
                ListTag<IntTag> pos = block.getListTag("pos").asIntTagList();
                BlockInfo info = allBlocks.get(i);

                if (info.downTrueFlatFalse == null)
                {
                    continue;
                }

                if (info.downTrueFlatFalse == true)
                {
                    if (i % 129 == 0)
                    {
                        newY = i % 129;
                        pos.set(1 , new IntTag(newY));
                    }
                    else
                    {
                        if (i < blocks.size() - 1) {
                            nextY = getY(blocks, i + 1);
                            pos.set(1, new IntTag(nextY + 1));
                        }
                    }
                }
                else if (info.downTrueFlatFalse == false)
                {
                    if (i % 129 == 0)
                    {
                        newY = i % 129;
                        pos.set(1 , new IntTag(newY));
                    }
                    else
                    {
                        if (i < blocks.size() - 1) {
                            nextY = getY(blocks, i + 1);
                            pos.set(1, new IntTag(nextY));
                        }
                    }
                }
            }

            int maxY = 0;
            for (int i = 0; i < blocks.size(); i++) {
                int y = getY(blocks, i);
                if (y > maxY) {
                    maxY = y;
                }
            }
            ListTag<?> sizeTag = root.getListTag("size");
            ListTag<IntTag> sizeIntTag = sizeTag.asIntTagList();
            sizeIntTag.set(1, new IntTag(maxY + 1));

            System.out.println("Adjusted structure height to: " + (maxY + 1));

            try (FileOutputStream fos = new FileOutputStream(output.toFile());
                 GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
                NBTSerializer serializer = new NBTSerializer(false);
                serializer.toStream(taggedMap, gzos);
            }
            System.out.println("Saved to: " + output.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! dang gotta fix your nbt because im 40% sure this program is perfect");
        }
    }
}