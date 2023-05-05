CAMERA API για QR

Δουλειές (προς το παρόν):
Χριστοδουλος: UI 
Λάζαρος: Database stuff
Σταύρος: Camera API

!!!!!!!!!!!!!!!!!!!!!!!UI!!!!!!!!!
Navigation Menu (Home/Sort by Date) για όλα τα activities

Main Activity:
Floating button για τα scans 
	one scan and return to home
	multiple scan and exit with return
	scan and then edit activity
Παρατεταμένο κλικ: Hold Menu για τα cards με επιλογές (Edit/Delete)
Κλικ: Click on card to View
checkboxes για πολλαπλή διαγραφή καρτών

Το addProductScreen θα γίνει χωρίς κουμπί και δυναμικά θα μπαίνει
Edit στο View mode και Save στο Edit Mode, επίσης θα απενεργοποιούνται
τα πεδία.

!!!!!!!!!!!!!DATABASE!!!!!!!!!!!!!!
{
Για RecyclerView φορτώνει 20 αποδείξεις πχ και πιάνουμε το event ότι έφτασε στο τέλος για να κάνουμε query τα επόμενα 20.
Μπορούμε να τεστάρουμε τι σημαίνει (αποδοτικά μιλώντας) το 20, το 50, το 100. Ξαναφορτώνουμε τα προηγούμενα + τα καινούργια Ή με κάποιο τρόπο φαίρνουμε μόνο τα επόμενα και τα κάνουμε append στον  πίνακα που χρησιμοποιούμε? πχ 20 1-20, 20μεχρι40 30 31 32

var a;

SELECT
FROM
WHERE id>a and limit(20)
ORDER BY
}

Βάση Δεδομένων πώς παίρνουμε τα data?? 
