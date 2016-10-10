# HomeTransfer
Data transfer across (wireless) LAN featuring high speed &amp; cross platform compatibility.

## Introduction
The scenario of big files or big chunks of files being transferred frome one computer to another does occur more often than we have time
available. From swapping of photos or videos after events to local copies at LAN partys there is always a need for USB-sticks or
external harddrives delivering sufficient performance. This is the main aspect which the HomeTransfer-project aims at.<br>
The Idea of HomeTransfer is to get rid of the storage medium in the middle by transferring data directly over the network both devices
are connected to.

## Details
### Cross-Platform
When spaking about cross platform, Java comes into ones' mind first. Therefore the initial version was written in Java, saving the need 
to create different versions for either Windows, Mac or Linux systems. However, not every System has Java installed so far. Native 
prototypes can be found at the following repositories:
<ul>
  <li>
    Windows C# implementation at <a href="https://github.com/JakobErpunkt/HomeTransferCS">HomeTransferCS</a>
  </li>
  <li>
    Mac Swift implementation at <a href="https://github.com/JakobErpunkt/HomeTransferSwift">HomeTransferSwift</a> (not yet)
  </li>
</ul>
The native implementations do not feature full commments and are mostly adapted ports from the Java version.

### Security
The whole project targets the use of files at home. Therefore no focus lies on security yet having all traffic sent unencrypted. The use 
of this program for confidential information is not intended and thus performed at the users own risk.
