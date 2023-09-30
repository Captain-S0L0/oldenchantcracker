# Old Enchantment Cracker

A tool for cracking and manipulating the RNG of enchanting in Minecraft for versions between Beta 1.9 Prerelease 4 and 1.7.10 to always get the enchantments you want.<br><br>
This is possible in a variety of ways, such as:
* From Beta 1.9 Prerelease 4 to 12w17b, the Random object that selects the random words displayed in the Standard Galactic Alphabet is seeded by the Random object that is later used by the enchanting container to actually enchant your items, at least in singleplayer.
* From Beta 1.9 Prerelease 4 to 12w21b, one can locate extremities produced by the RNG to obtain enough data out of just the levels themselves. Works in multiplayer, but takes a while, so if galactic is available, use that.
* From 12w22a to 1.7.10, one can disable the bookshelves powering the table to reduce the level call to a simple 1 + random.nextInt(8), and then use an item dropped from the GUI to trigger a pressure plate and re-enable the bookshelves while staying inside the enchanting GUI, both in singleplayer and multiplayer


Plug that data into the wonderful [LattiCG](https://github.com/mjtb49/LattiCG), and presto, you now have the enchanting seed. Combine that with an enchantment predictor, and you have a pretty powerful tool.<br><br>
A video explaining how to use this tool can be found [here](https://www.youtube.com/watch?v=AufoTbXW3_0).<br><br>


If you have any issues make sure to do the following:
* You are running both Minecraft and the finder with the same Java version, as changes in Java's HashMap functions can influence the enchanting process.
* You are NOT drag-clicking the item inside the GUI slot in 1.5+.
* Use the ROBOT option if the ALT+PRTSCR searcher is being problematic.


If you still have problems, feel free to contact me on Discord @captain_s0l0, or open an issue.
