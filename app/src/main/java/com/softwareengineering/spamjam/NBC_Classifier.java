package com.softwareengineering.spamjam;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Deepak on 04-11-2017.
 */

public class NBC_Classifier {

    static Hashtable<String,Double> spamWords = new Hashtable<>();
    static Hashtable<String,Double> hamWords = new Hashtable<>();
    static int spamCount = 0;
    static int hamCount = 0;

    public static String getString(){
        String str = "spam\tAll GoAir flights will now operate from Terminal 2 at the Indira Gandhi International Airport, Delhi with effect from 29th Oct'17\n" +
                "spam\tLove, Deceit and Suspense. Watch Vikram Bhatt's Original Web Series, Maaya and Twisted on JioCinema! Click http://jio.com/r/MOnDhaTY to download now.\n" +
                "spam\tGet 100% cashback (8 vouchers of Rs.50 each in MyJio), plus additional cashback of Rs.101 for 1st recharge and Rs.20 for subsequent recharges through Amazon Pay with Rs399. Recharge Now on http://jio.com/r/LT1z6r6uto Reserve the BEST TARIFFS for another 3 months. T&C apply.\n" +
                "spam\tDear Customer,Use Code: INTEMT & get upto Rs.20,000 instant discount on international Air-tickets.Care Team, Email: care@EaseMyTrip.com http://bit.ly/intemt\n" +
                "spam\twww.EaseMyTrip.com - Lowest Price Guarantee on domestic & international flights Use Code: INTEMT & get upto Rs.20,000 instant discount on International flights\n" +
                "spam\twww.EaseMyTrip.com - Lowest Price guarantee on domestic & international Flights We guarantee to provide lowest Air-Fare or else We pay you double of difference\n" +
                "spam\tJioPhone prebooking starts today at 5:30 PM. Beat the rush, be among the 1st to get JioPhone. Visit Jio.com, MyJio or your nearest Jio Retailer. Click http://jio.com/r/FU4xq6dD to prebook.\n" +
                "spam\tPREBOOK a JioPhone for you and your dear ones on this auspicious day! Visit Jio.com, MyJio or your nearest Jio Retailer. Click http://jio.com/r/FZLOTv4s to prebook.\n" +
                "spam\tThank You for being part of Jio Family - Stay connected on the largest and strongest 4G network of India which gives you high speed internet services and\n" +
                "spam\tYour first app is ready. Install it and sign in to link to your PC: https://aka.ms/ay?s=2&c=3df2aea3-4976-0001-c2d9-f23d7649d301\n" +
                "spam\tGet 100% cashback (8 vouchers of Rs.50 each in MyJio), plus additional cashback of Rs.100 for 1st recharge and Rs.30 for subsequent recharges through PhonePe with Rs399. Recharge Now on http://jio.com/r/LXsLZ0Bj to Reserve the BEST TARIFFS for another 3 months. T&C apply.\n" +
                "spam\tJeeto Rs 50000 SONA bilkul Free Q. Punjab ka prasiddha tyohar? A)Onam B) Lohri Reply A ya B. Call 552560 (FREE)\n" +
                "spam\tFlat Rs.100 Cashback* on your first ever Jio Recharge! Download PhonePe, India's payments app- http://bit.ly/JioPP  *TnC apply\n" +
                "spam\tJeeto Rs 50000 SONA bilkul MUFT Q. Punjab ki Rajdhani kya hai? A) Srinagar B)Chandigarh  Reply A ya B Call 552560 tollFree\n" +
                "spam\tSpicejet & Vistara Sale: Air-Fares starting at Rs.999 Travel Period: Till April,2018 www.EaseMyTrip.com Hurry! Book now @ Zero Convenience fees.\n" +
                "spam\tEaseMyTrip Diwali Offer Get upto Rs.2000 OFF on Air-Tickets to fly before 20th October.Use Promo Code: EASEFLY www.EaseMyTrip.com Email: care@easemytrip.com\n" +
                "spam\tChandigarh, your uberPOOL has arrived! #SwitchToPool & enjoy up to 40% cheaper fares than uberGO! uberPOOL = Paisa Vasool! Read: t.uber.com/poolchd\n" +
                "spam\tUse TRAI's DND 2.0 App to effectively complain against silence spam Calls and SMS: https://goo.gl/jMvmeB. From: TRAI\n" +
                "spam\tWIN CAR RC 12 mein 360 SMS MUFT, 28 dino ke liye, ya DIAL *369*12# aur saath mein paayein mauka dheero Inaam jeetne kaCall 566940 (TOLLFREE) aur banayein Dost\n" +
                "spam\tGo cashless anytime, anywhere with BHIM. Download BHIM TODAY!!! Click here bit.ly/2ujQmwe\n" +
                "spam\tPublic is hereby cautioned to be extra careful & to verify the credentials of any person asking money/advance payment for mobile tower installation in your premises. Department of Telecom & Team Jio\n" +
                "spam\tDear Customer,as part of Govt. Of India’s Cyber Swachhta Awareness initiative, please visit http://www.cyberswachhtakendra.gov.in to get the latest information about malware, security best practices, countermeasures, security tools and download ‘Free Bot Removal Tool’ to secure / disinfect your systems.\n" +
                "spam\tMajority of people around you use uberMOTO for short trips to the market, to work or to a friend's house. Give it a try today. Fares from Rs20 only- Uber\n" +
                "spam\tHave you Moto'd yet? Get around town for Rs20 flat on uberMOTO today. Valid on uberMOTO trips upto 5kms in the tricity until Sep10 only -UberCHD\n" +
                "spam\tFlat fares are back! Moto around tricity on a 2 wheeler for Rs 20 only. Valid on uberMOTO trips upto 5 kms for this weekend only. Uber\n" +
                "spam\tDear Customer,Download all premium Jio Apps in single click & get access to Live TV, Movies, Music, Magazines, News, Cloud storage & much more. Click now at https://www.Jio.com/GetMyJio Thank you, Team Jio\n" +
                "spam\tSave Rs 250 on your next 5 Uber trips! Click https://m.uber.com/ul/?action=applyPromo&promo=TRAVEL250 and get 50% off on 5 Uber trips. Max disc. Rs 50/ride.\n" +
                "spam\tDHAMAKA RC 12 mein 360 SMS MUFT,28 dino ke liye, ya DIAL *369*12# aur saath mein paayein mauka dheero Inaam jeetne ka.Call 566940 (TOLLFREE) aur banayein Dost\n" +
                "spam\tIDEA FESTIVE OFFER RC 12 mein 360 SMS MUFT 28 dino ke liye, ya DIAL *369*12# saath mein mauka dheero Inaam jeetne ka.Call 566940 (TOLLFREE) aur banayein Dost\n" +
                "spam\tUnlimited Dhamaka!Rechrg on MyIdea App.179=Unlmtd Loc/STD +1GB 3G/4G data .Val-28 days.TnC.Click http://m.onelink.me/8e1d335a\n" +
                "spam\tDhamaka offer!Chance to win Rs100 AmazonVoucher by recharging on MyIdea app.179=Unltd Loc/STD+1.25GB 4G/2Gdata.Val-28D.TnC.Click http://m.onelink.me/8e1d335a\n" +
                "spam\tDiwali Exclusive!Chance to win Rs100 AmazonVoucher by recharging on MyIdea app.179=Unltd Loc/STD+1.25GB 4G/2Gdata.Val-28D.TnC.Click http://m.onelink.me/8e1d335a\n" +
                "spam\tWIN GOLD RC 12 mein 360 SMS MUFT, 28 dino ke liye, ya Dial *123# for Missed Call Alert Packs, Adhik jankari ke liye dial 1212 tollfree.\n" +
                "spam\tUnlimited Dhamaka offer !! Rs.404 mein payein Unlimited Local/STD calling aur 56 GB 3G/4G - Har roj 1 GB 4G/3G internet pure 56 din ke liye\n" +
                "spam\tE-statement Alert: You have 606 Reward Points as on Sep'17 in your State Bank Rewardz A/c. Download the app http://bit.ly/2yNhHd9  and earn 100 bonus points on first time login or sign up! T&C\n" +
                "spam\tNewly Added TV Shows for you: Bigg Boss 11, Woh Apna Sa, Aisi Deewangi Dekhi Nahi Kahi and Ek Deewana Tha. Click http://jio.com/r/MUoGfofG to watch now.\n" +
                "spam\tJioFi at 50% off. Celebrate the Festive Season with JioFi - ONLY Rs.999. Rush to nearest Jio Store TODAY or click http://jio.com/r/K9yQEqlZ to buy NOW!\n" +
                "spam\tGet a JioFi at half the price! Enjoy the JioFi Festive Celebration and get your JioFi for Rs. 999/- only. Hurry! Limited period offer from 20th -30th Sept only. Rush to nearest Jio Store TODAY or click http://jio.com/r/KvJGs155 to buy NOW!\n" +
                "spam\tiPhone 8 comes to the world's largest data network - Jio. Pre-order it now and get a 70% BuyBack. Visit http://jio.com/r/Kp9vloOP now\n" +
                "spam\tJoin 1 Lakh GTD Tournament Tonight at 9 PM with buyin Rs.550 only at PokerNation.com. Register Now! https://goo.gl/sMYiFB\n" +
                "spam\tBoost your Bankroll this Diwali !! Join 14 K GTD  Freeroll tournament Tonight at 9 PM only at PokerNation.com. Register Now!! https://goo.gl/sMYiFB\n" +
                "spam\tBig Freeroll! Join 14K GTD Freeroll tournament tonight at 9 PM only at PokerNation.com Register now!! Desktop/Mobile https://goo.gl/sMYiFB\n" +
                "spam\t1 Lakh Sunday GTD | Rank 1 gets 50K GTD Play and win all cash 1 Lakh GTD tonight at 9 PM on Pokernation.com.\n" +
                "spam\tRs.550 only for 1 Lakh GTD. Now enjoy reduced buy-in for 1 lakh GTD every Sunday 9 PM! Register Now https://goo.gl/2gPKfP\n" +
                "spam\tPANTALOONS presents WINTER SHOPPE. New winter collection in stores. Shop & get branded gifts - Borosil Dinner Set worth 2250, F&S cutlery set worth 1495.TC\n" +
                "spam\tCelebrate Dhanteras @ Pantaloons! Get a Handcrafted 24K Gold & Silver Plated Laxmi & Ganesh Idol worth 2499 on shopping of 6000 for shagun of 11. TC\n" +
                "spam\tDil mein Diwali, Style mein Pantaloons! Exciting offer till 2 OCT ONLY. Get GIFT VOUCHERS upto Rs 2000 + Skybags Large Trolley or Cabin Bags, Borosil Dinner Set, FNS Cultery Set. Click bit.ly/PantaloonsOffers1 .TC\n" +
                "spam\tCelebrate Dussehra with PANTALOONS &Branded Gifts! Club purchases across visits for Skybags Large Bag, Skybags Cabin Bag, Borosil Dinner Set& FNS Cutlery Set.TC\n" +
                "spam\tThis Diwali, gift yourself & your loved ones a Domino's Pizza. Just spin the Domino's 'Wheel of Gift' & win exciting offers. Click on http://bit.ly/2xLh0Pq T&C\n" +
                "spam\tCongratulations! You have received 8 MyJio vouchers worth Rs. 50 each which you can use for the payment of subsequent recharges post 15th Nov, 2017.Please note that these vouchers can be used only on the recharges done via MyJio App. T&C Apply.\n" +
                "spam\tSpl Diwali Launch: Ultra Light Eyewear Styles at Lenskart.com Shop SLIM Eyeglasses from John Jacobs & Get 19% OFF +19% Cashback *TnC Visit Store: lenskart.com/s\n" +
                "spam\tCheck your Jio Data balance and manage your account on the go. Click http://jio.com/r/LvpgJHhr to know more.\n" +
                "spam\tThank you for being part of Jio family - The largest and strongest 4G network of India which gives you high speed internet services and unlimited calls anytime and every time across the country. Recharge renewal date of your Jio number 7986424347 is approaching. Click http://jio.com/r/Lonloapj to recharge now and avoid last minute rush.\n" +
                "spam\tStudy MBBS in AMERICA/EUROPE/PHILIPINES/CHINA/BANGLADESH, Direct Admision Lowest Package Call: 9810666250/ 011-40527629/ info@eduquanta.com\n" +
                "spam\tYou have Rs.2000 in your goCash wallet. Download the goibibo app or sign up to claim and save on your travel bookings. http://go.ibi.bo/W1ad/VFDdxo97IG\n" +
                "spam\tPre-Book Your Meals For Train Journey This Puja Festival,Get Flat Rs. 50 Off From RailRestro. Use Coupon: \"\"NAVMEAL50\"\". Order Food Today @ https://goo.gl/hnCJpM\n" +
                "spam\tCongratulations on your new 4G phone! To enjoy Vodafone SuperNet 4G speeds at 3G prices, insert your 4G SIM into slot 1 & select network type as 4G/LTE. If you haven't upgraded to a 4G SIM yet, visit our nearest store for an instant upgrade & enjoy a welcome data offer absolutely free! No documents required.?\n" +
                "spam\tYou have Rs.2000 in your goCash wallet. Download the goibibo app or sign up to claim and save on your travel bookings. http://go.ibi.bo/W1ad/nXF8YabPbG\n" +
                "spam\tE-statement Alert: You have 591 Reward Points as on Aug'17 in your State Bank Rewardz A/c. Download the app goo.gl/x35EKU and earn 100 bonus points on first time login or sign up! T&C\n" +
                "spam\tHey Aashirwad, you've won 5 FREE UBER rides! Apply promo MYBEST & enjoy Rs.50 off 5 Uber rides in CHD tricity till Friday. How to apply promo t.uber.com/edn\n" +
                "spam\tMOTO flat fares are back! Ride anywhere in the city for Rs 10 only. Valid on uberMOTO rides upto 5 kms until Oct 15. No promo required. UberCHD\n" +
                "spam\tWoohoo Aashirwad, UBER Flat Fare Rs.40 only for you! Pay no more than Rs.40 on all uberGO rides upto 7km anywhere in Chandigarh tricity. No promo needed.\n" +
                "spam\tYour plan for Jio no.7986424347 will expire on 17-10-2017. Recharge IMMEDIATELY with Rs.509 plan today and enjoy 2GB high speed data/day and unlimited calling benefits for 2 months. Click http://jio.com/r/LMJieVcf to recharge.\n" +
                "spam\tGet a JioFi at half the price! Enjoy the JioFi Festive Celebration and get your JioFi for Rs. 999/- only. Hurry! Limited period offer from 20th -30th Sept only. Rush to nearest Jio Store TODAY or click http://jio.com/r/KCHlndBh to buy NOW!\n" +
                "inbox\tHi, the validity of your internet pack has expired. Continue to browse at 4p/10Kb.  Buy a data pack from MyVodafone App (www.vodafone.in/a4) or dial *111*6#\n" +
                "inbox\tHello, your 2G internet pack is now active & valid till 10.10.2017 23:59:59. You have 200.00 MB in your account, track usage at www.vodafone.in/j1 or dial *111*2*2#\n" +
                "spam\tAmazing offers on 4G PHONES with Vodafone. UNLIMITED Calling + EXTRA Data, 4 Times Data on Packs, 9GB FREE Data. To know more click  http://tinyurl.com/jr5zmfd\n" +
                "spam\tHi, the validity of your internet pack has expired. Continue to browse at 4p/10Kb.  Buy a data pack from MyVodafone App (www.vodafone.in/a4) or dial *111*6#\n" +
                "inbox\tYour A/c XXXXXXX6332 has been debited with INR 500.00 on 27/10/17 towards NEFT with UTR SBIN917300424635 sent to Hindustan Institute of Technology IOBA0002554\n" +
                "inbox\tYour A/c XXXXXXX6332 has been debited with INR 1000.00 on 27/10/17 towards NEFT with UTR SBIN917300424634 sent to Deepak Kumar\n" +
                "inbox\tOrder Placed: Your order for Flipkart SmartBuy Temp...+1 more product with order id OD110580348074003000 amounting to Rs.522 has been received.You can expect delivery by Mon 30, Oct 2017.We will send you an update when your order is packed/shipped.You can manage your order here http://fkrt.it/PvoylTuuuN .\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 53.0 on Date 2017-10-24 04:58:22 PM by UPI Ref No 729716407782\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 51.0 on Date 2017-10-24 04:56:46 PM by UPI Ref No 729716406182\n" +
                "inbox\tUse 788866 as your login OTP. OTP is confidential. Paytm never calls you asking for OTP. Sharing it with anyone gives them full access to your Paytm Wallet.\n" +
                "inbox\tUse 648687 as your login OTP. OTP is confidential. Paytm never calls you asking for OTP. Sharing it with anyone gives them full access to your Paytm Wallet.\n" +
                "inbox\tUse 648687 as your login OTP. OTP is confidential. Paytm never calls you asking for OTP. Sharing it with anyone gives them full access to your Paytm Wallet.\n" +
                "inbox\tYour transaction of Rs.500 via PhonePe has failed. If the amount has been deducted, it will be credited back in 5 working days.\n" +
                "inbox\tSpeedPost Article ER934014563IN booked on 17/10/2016 was delivered on 21/10/2016 Thank you for using India Post. www.Indiapost.gov.in.\n" +
                "inbox\tYour Paytm wallet balance on 27-10-17 is Rs 0.0. Scan Paytm QR code & pay at nearby shops,1000 lucky winners get 100% Cashback daily. T&C http://m.p-y.tm/LT7\n" +
                "inbox\tOut for Delivery: Flipkart SmartBuy Flat... with tracking ID FMPC0248283977 from flipkart.com, will be delivered before 7pm today by an EKART Wish Master (call 07529054000, PIN 106). Please pay Rs.229.0 by cash or card.\n" +
                "inbox\tDelivered: Flipkart SmartBuy Flat... with tracking ID FMPC0248283977 from flipkart.com was delivered to your friend,  vikram, today. Click to give feedback: http://ekrt.in/Eq2Mw!NNNN .\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 547.0 on Date 2017-10-24 09:01:48 PM by UPI Ref No 729721643212\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 558.0 on Date 2017-10-29 11:47:24 AM by UPI Ref No 730211302647\n" +
                "inbox\tShipped: Your Miss & Chief Mini Raci... with order id OD110515627422722000 has been shipped and will be delivered  by Wednesday, Oct 25, 2017. You will receive another SMS when the Wishmaster is out to deliver it. Track your shipment here http://fkrt.it/EWVP0!NNNN\n" +
                "inbox\tYou've received Rs.500 from MADAN via PhonePe into your State Bank Of India a/c.\n" +
                "inbox\t900270 is your One Time Password for online purchase of Rs 26.00 at recharge_paytm_PayTM thru State Bank Debit Card ending 1084. Don't share this with anyone\n" +
                "inbox\t240597 is your One Time Password for online purchase of Rs 47.00 at recharge_paytm_PayTM thru State Bank Debit Card ending 1084. Don't share this with anyone\n" +
                "inbox\t716388 is your One Time Password for online purchase of Rs 50.00 at recharge_paytm_PayTM thru State Bank Debit Card ending 1084. Don't share this with anyone\n" +
                "inbox\tYou added a third party on 26-10-2017 12:47:51 PM. If it is not added by you, please lock the User Access urgently to avoid misuse\n" +
                "inbox\tactivation of 3rd party added by you at 26-10-2017 12:47:51 PM is in process. Money can be transferred only after activation\n" +
                "inbox\t3rd party added by you on 26-10-2017 12:47:51 PM is ACTIVATED. Do not share your SMS password with any one\n" +
                "inbox\tYour A/C XXXXX376332 Credited INR 6,000.00 on 27/10/17 -Deposit by transfer from Mr. DEEPAK KUMAR . . A/C Bal INR 6,508.52\n" +
                "inbox\tThank you for using your SBI Debit Card 459XX1084 for a purchase worth Rs6500.0 on POS  000162017241699 at H.M.T RESORTS txn# 730016101504.\n" +
                "inbox\tThank you for using your SBI Debit Card 459XX1084 for a purchase worth Rs499.0 on POS  AMAZON at AMAZON txn# JU5760265942.\n" +
                "inbox\tThank you for using your SBI Debit Card 459XX1084 for a purchase worth Rs60.0 on POS  80600004 at recharge_paytm_PayTM txn# 729070019870.\n" +
                "inbox\tThank you for using your SBI Debit Card 459XX4047 for a purchase worth Rs179.0 on POS  80600004 at recharge_paytm_PayTM txn# 729840007004.\n" +
                "inbox\tRs 543.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-27. Info: UPI/P2A/730012267949/vikramvicky1119@okhdfcbank\n" +
                "inbox\tRs 925.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-24. Info: UPI/P2A/729719554213/jasdeep096@okhdfcbank\n" +
                "inbox\tRs 100.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-17. Info: UPI/P2A/729001266050/merahulroshan@okhdfcbank\n" +
                "inbox\tRs 123.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-01. Info: UPI/P2A/727420814791/deepak199801@okhdfcbank\n" +
                "inbox\tRs 500.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-18. Info: UPI/P2A/729116645230/mt96384@okhdfcbank\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 2000.0 on Date 2017-10-27 12:07:15 AM by UPI Ref No 730000577021\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 100.0 on Date 2017-10-17 02:04:24 PM by UPI Ref No 729014383914\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 115.0 on Date 2017-10-10 10:48:08 AM by UPI Ref No 728310418310\n" +
                "inbox\tDear SBI UPI User, Your a/c no. XXXXXX7449 is credited with INR 111.00 on 2017-10-09 12:56:13 AM for reversal of transaction (UPI Ref no 728200117144).\n" +
                "inbox\tDear SBI UPI User, your account is debited INR 69.0 on Date 2017-10-01 08:41:06 PM by UPI Ref No 727420684343\n" +
                "inbox\tDear Customer, You have a missed call from +918699020183 . The last missed call was at 06:27 PM on 23-Oct-2017 . Thankyou, Team Jio.\n" +
                "inbox\tThank you for using State Bank Internet Banking. Your Transaction Ref No IGACFBTUP0 for Rs.865.0 on 17-Oct-17 11:17\n" +
                "inbox\tIndiaideas has requested Rs.399 from you via PhonePe. To pay instantly click here http://phon.pe/m7v9bbws\n" +
                "inbox\tYou've received Rs.37 cashback from PhonePe into your wallet for cashback. Current wallet balance is Rs.37\n" +
                "inbox\tYou've sent Rs.10000 to Rahul Roshan via PhonePe from your bank a/c.\n" +
                "inbox\tYou've received Rs.500 from Deepak Kumar via PhonePe into your State Bank Of India a/c.\n" +
                "inbox\tDear Customer, +919110672926 is now available to take calls.\n" +
                "inbox\tYou've received Rs.10000 from Rahul Roshan via PhonePe into your State Bank Of India a/c.\n" +
                "inbox\tYou've received Rs.1 from Rahul Roshan via PhonePe into your State Bank Of India a/c.\n" +
                "inbox\tYour A/C XXXXX377449 has a credit by Transfer of Rs 15.17 on 22/09/17. Avl Bal Rs 49,766.62.Download Buddy@ http://goo.gl/qUlXqL\n" +
                "inbox\tOTP for Aadhaar (XXXX9064) is 284873 and is valid for 30 minutes. (Generated at 2017-09-21 3:28:20)\n" +
                "inbox\tDear Customer, You have a missed call from +919464087760 . The last missed call was at 08:57 PM on 24-Aug-2017 . Thankyou, Team Jio.\n" +
                "inbox\tDear Customer, You have a missed call from +917696197347 . The last missed call was at 10:33 AM on 14-Jul-2017 . Thankyou, Team Jio.\n" +
                "inbox\tUse SBI ATMs for better security, convenience & faster complaint resolution. As per RBI directive, more than 3 txns in metro & 5 in non-metro are chargeable\n" +
                "inbox\tRs 1500 withdrawn from A/c xxxx 7449 on 160917 at BOM ATM  DA128801.Txn# 725922231125 .Avl bal Rs 59251.45.\n" +
                "inbox\tFlipkart's Big Billion Days are here! 10% Cashback* on purchases via PhonePe. Extra 10% off with SBI Debit & Credit Card payments. TnC https://phon.pe/launchApp\n" +
                "inbox\tOrder Placed: Your order for Maniac Solid Men's Rou... with order id OD110296872422604000 amounting to Rs.369 has been received.  You can expect delivery by Fri 06, Oct 2017.  We will send you an update when your order is packed/shipped.You can manage your order here http://fkrt.it/lSRK8TuuuN .\n" +
                "inbox\tShipped: Your Maniac Solid Men's Rou... with order id OD110296872422604000 has been shipped and will be delivered  by Friday, Oct 06, 2017. You will receive another SMS when the Wishmaster is out to deliver it. Track your shipment here http://fkrt.it/Yk5bd!NNNN\n" +
                "inbox\tCashback added: A cashback of Rs. 37 has been credited to your PhonePe Wallet and can be used to buy products on Flipkart.\n" +
                "inbox\tDear Customer, You have a missed call from +919924899544 . The last missed call was at 06:36 PM on 27-Sep-2017 . Thankyou, Team Jio.\n" +
                "inbox\tDear Customer, +919924899544 is now available to take calls.\n" +
                "inbox\tDelivered: Maniac Solid Mens Roun... with tracking ID FMPP0111602792 from flipkart.com was delivered,  today. Click to give feedback: http://ekrt.in/Llv ~HTuuuN .\n" +
                "inbox\tOut for Delivery: Maniac Solid Mens Roun... with tracking ID FMPP0111602792 from flipkart.com, will be delivered  before 7pm today by an EKART Wish Master .\n" +
                "inbox\tRs 333.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-24. Info: UPI/P2A/729723708849/goog-payment@okaxis\n" +
                "inbox\tRs 89.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-09. Info: UPI/P2A/728200900614/vikramvicky1119@okhdfcbank\n" +
                "inbox\tRs 600.00 credited to account XXXXX7449 of SBIN linked to goldie3may@okaxis on 2017-10-07. Info: UPI/P2A/728020286467/mt96384@okhdfcbank\n" +
                "inbox\tOTP for transaction IRCTC-RAILWAY TICKET BOOKING is:71396462. Do not share it with anyone\n" +
                "inbox\tUse SBI ATMs for better security, convenience & faster complaint resolution. As per RBI directive, more than 3 txns in metro & 5 in non-metro are chargeable\n" +
                "inbox\tThank you for using your SBI Debit Card 459XX1217 for a purchase worth Rs1549.0 on POS  470000075820247 at OCTAVE txn# 730114758775.\n" +
                "inbox\tRs 1500 withdrawn at SBI ATM  S10A006342003  BELA CHOWK, ROPAR , ROPAR from A/c xx 6467 on 28/10/17.Txn#1595 .Avl bal Rs 1917.51.\n" +
                "inbox\tRecharge done on 14-10-2017 at 07:41PM,MRP: Rs 30.00, PF: Rs2.10,GST 18% payable by Company/Distributor/Retailer:Rs4.58,Talktime: RS23.32,Balance: Rs23.33 TransID 69179118\n" +
                "inbox\tRecharge done on 23-10-2017 04:02PM,MRP:Rs200.00,PF:Rs3.00,GST 18% payable by Company/Distributor/Retailer:Rs30.51,Talktime:RS176.16,Bal:Rs176.16 TransID75253667.Link your Airtel no. with Aadhaar now.\n" +
                "inbox\tLink your Airtel number with Aadhaar now. Ignore if already linked.\n" +
                "inbox\tDear customer,your transaction at POS PAYUST000001653 has been declined due to insufficient fund in your account.\n" +
                "inbox\t693475 is your One Time Password for online purchase of Rs 100.00 at airtel18/pb/prepaid thru State Bank Debit Card ending 1217. Don't share this with anyone\n" +
                "inbox\t304301 is your One Time Password for online purchase of Rs 70.00 at airtel18/pb/prepaid thru State Bank Debit Card ending 1217. Don't share this with anyone\n" +
                "inbox\t280570 is your One Time Password for online purchase of Rs 30.00 at airtel18/pb/prepaid thru State Bank Debit Card ending 1217. Don't share this with anyone\n" +
                "spam\tWelcome to Delhi on Airtel-India's Fastest Network. Enjoy unlimited incoming calls FREE as you roam anywhere in India.Data rates same as home network.For discounted outgoing rates, dial *121*14#";

        return str;
    }

    public static void fillTable(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham)  throws IOException
    {
        String message;
        spamWords.clear();
        hamWords.clear();

        String[] file = getString().split("\n");

//        Log.e("lines", file.length + "");
        String line;
        for(int i = 0; i < file.length; i++){
            line = file[i].toLowerCase();
            String [] spliter = line.split("\\t");

            if(spliter[0].equals("inbox"))
            {
                String result = MessageCleaning.messageCleaning(spliter[1]);

                String [] msgWords = result.split("\\s+");


                hamCount += msgWords.length;

                for(String s : msgWords)
                {
                    if(!hamWords.containsKey(s))
                    {
                        hamWords.put(s, 1.0);
                    }
                    else
                    {
                        hamWords.put(s, hamWords.get(s)+1);
                    }
                }
            }
            else
            {
                String result = MessageCleaning.messageCleaning(spliter[1]);
                String [] msgWords = result.split("\\s");
                spamCount += msgWords.length;

                for(String s : msgWords)
                {
                    if(!spamWords.containsKey(s))
                    {
                        spamWords.put(s, 1.0);
                    }
                    else
                    {
                        spamWords.put(s, spamWords.get(s)+1);
                    }
                }

            }
        }

        Log.e("lines", "ham Count = " + hamCount);
        Log.e("lines", "spam Count = " + spamCount);
        Set<Integer> keys = Ham.keySet();
        for (int key : keys){
            message = Ham.get(key).toLowerCase();
            String lang = Language_Filter.predictor(message);

            if(lang.equals("English")) {
                message = MessageCleaning.messageCleaning(message);
                String[] msgWords = message.split("\\s+");

                hamCount += msgWords.length;

                for (String s : msgWords) {
                    if (!hamWords.containsKey(s)) {
                        hamWords.put(s, 1.0);
                    } else {
                        hamWords.put(s, hamWords.get(s) + 1);
                    }
                }
            }
        }

        keys = Spam.keySet();
        for (int key : keys){
            message = Spam.get(key).toLowerCase();
            String lang = Language_Filter.predictor(message);

            if(lang.equals("English")) {
                message = MessageCleaning.messageCleaning(message);
                String[] msgWords = message.split("\\s+");

                spamCount += msgWords.length;

                for (String s : msgWords) {
                    if (!spamWords.containsKey(s)) {
                        spamWords.put(s, 1.0);
                    } else {
                        spamWords.put(s, spamWords.get(s) + 1);
                    }
                }
            }
        }

        Set<String> keySet = hamWords.keySet();
        for(String s: keySet)
        {
            hamWords.put(s, hamWords.get(s)/hamCount);
        }

        keySet = spamWords.keySet();
        for(String s: keySet)
        {
            spamWords.put(s, spamWords.get(s)/spamCount);
        }

        Log.e("lines", "ham Count = " + hamCount);
        Log.e("lines", "spam Count = " + spamCount);
    }

    public static int classifier(String message)
    {
        String lang = Language_Filter.predictor(message);
        if(lang.equals("English")) {
            message = MessageCleaning.messageCleaning(message);
        }
        String[] msgWords = message.split("\\s+");
        double hamProb = hamCount * 1.0 / (hamCount + spamCount);
        double spamProb = spamCount * 1.0 / (spamCount + hamCount);

        for (String s : msgWords) {
            if (spamWords.containsKey(s)) {
                spamProb *= spamWords.get(s);
            } else {
                spamProb *= (1.0 / spamCount);
            }


            if (hamWords.containsKey(s)) {
                hamProb *= hamWords.get(s);
            } else {
                hamProb *= (1.0 / hamCount);
            }

        }

        if (hamProb >= spamProb)
            return Message.NOT_SPAM;
        else
            return Message.SPAM;
    }

    public static HashMap<Integer, Integer> classify(HashMap<Integer, String> Spam, HashMap<Integer, String> Ham, HashMap<Integer, String> dataSet) throws IOException{

        fillTable(Spam, Ham);

//        Set<String> keys_ = spamWords.keySet();
//        for (String key : keys_) {
//            Log.d("Probab Spam", key + " : " + spamWords.get(key));
//        }
//        keys_ = hamWords.keySet();
//        for (String key : keys_) {
//            Log.d("Probab Ham", key + " : " + hamWords.get(key));
//        }

        HashMap<Integer, Integer> spam_or_ham = new HashMap<>();

        Set<Integer> keys = dataSet.keySet();
        for (int key : keys) {
            //Log.e("Red", key + " : " + dataSet.get(key));
            String message = dataSet.get(key).toLowerCase();
            spam_or_ham.put(key, classifier(message));
        }

        return spam_or_ham;
    }

}