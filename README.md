# Old Minecraft Enchantment Cracker

A tool for cracking and manipulating the RNG of enchanting in singleplayer Minecraft for versions between Beta 1.9 Prerelease 4 and 12w17a (1.3 snapshot) to always get the enchantments you want.<br><br>
This is possible as the Random object that selects the random words displayed in the Standard Galactic Alphabet is seeded by the Random object that is later used by the enchanting container to actually enchant your items.
Plug that data into the wonderful [LattiCG](https://github.com/mjtb49/LattiCG), and presto, you now have the enchanting seed. Combine that with an enchantment predictor, and you have a pretty powerful tool.<br><br>
**This only works in singleplayer**, as the enchanting container used by the client and the "server" are the same object, thus the Random is the same. This breaks with the move to the internal server,
as the container object that seeded the enchanting GUI was no longer shared.<br><br>
A short video explaining how to use this tool can be found [here](https://youtu.be/elcx1i7Zauc).<br><br>
A new function has been added that allows for obtaining your enchantments in less advances that is NOT convered
in that tutorial. To enable, tick the "Advanced Advances" box on the manipulation tab. From there, you need to know
what to do with the results, and they are as follows:
* "Swap Different" - Swap the item with something else that is enchantable.
* "Shift Remove" - Shift click the item out of the table, and put it back in again.
* "Swap Same" - Swap the item with another one of itself that is not enchanted.
* "OutIn" - Left-click the item out and back into the slot.

If you have any issues, make sure you are running both Minecraft and the finder with the same Java version, as changes in Java's HashMap functions can influence the enchanting process.
If you still have problems, feel free to contact me on Discord @captain_s0l0, or open an issue.
