# MapLattice
Awesome little project to make building map arts a little easier. First project so not too advanced and the code also isn't great.

UPDATE incoming, code makes maparts shading messed up every chain as the last block of every up chain is down, I know the fix will take some time

I think I will use this to just explain the project,

So in its initial state right now, it does 2 things. It scans the whole map art file (128 by 129 square) and tags all the blocks as "down" or "flat" and if they are moving up, it puts them
on a linear line where y = x. 

After the code then goes from the back of the file, from the very last block moving backwards it builds branches of blocks off the main structure, and they are all flat or down so its easy
to create.

Some improvements I might add in the future are:
- Grouping downward staircases and flat sections together (would be very useful but also I'm not sure how to implement it extatically)
- Adding a GUI

HOW TO USE:

Download the file, open it using IDEA, (needs maven) place your map art in the "schematics" section of the project,
Run the program, and type in the name of your file
Drag the edited file into your schematics folder in Minecraft and your done.

If you want to contribute that would be much appreciated and welcome.
