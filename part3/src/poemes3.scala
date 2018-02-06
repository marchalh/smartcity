/*
 *	NOTE SUR LES CORPUS
 *	Pour faire fonctionner les autres corpus, il faut corriger le dictionnaire (qui comprend des nombres de syllabes a virgule). Ensuite, il faut ajouter un argument implicite a Source.fromFile qui specifie l'encodage. Il faut le mettre en UTF-8. Sur Linux, c'est de base, donc pas besoin de l'ajouter. Il faut le faire uniquement pour windows
 *
 *
 */
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import scala.util.{Failure, Random, Success}

object Main {
    val system = ActorSystem("Poemes")
    
    def main(args:Array[String]){
    	println(Console.GREEN + "*********************************************" + Console.RESET)
    	println(Console.GREEN + "** Bienvenue dans le generateur de poeme ! **" + Console.RESET)
    	println(Console.GREEN + "*********************************************" + Console.RESET)
    	println
    	println(Console.BLUE + "Veuillez patientez quelques instants ..." + Console.RESET)
    	println
        val chemin_corpus1 = "raquin.txt"
        val chemin_corpus2 = "zola.txt"
        val chemin_dictionnaire = "dicorimes.dmp"
        val poete1 = system.actorOf(Props(new Poete(chemin_corpus1, chemin_dictionnaire)), "poete1")
        val poete2 = system.actorOf(Props(new Poete(chemin_corpus2, chemin_dictionnaire)), "poete2")
        val maitre = system.actorOf(Props(new Maitre(poete1, poete2)), "maitre")
        implicit val timeout = Timeout(100000 millis)
        for {
        	i <- 0 until 4
        } maitre ! askStrophe()
    }
}

class Maitre(poete1:ActorRef, poete2:ActorRef) extends Actor {
    var nbr = 0

    def receive = {
        case askStrophe() => {
        	nbr = nbr + 1
            implicit val timeout = Timeout(50000 millis)
            traiterReception((takeOnePoete ? newDeuxVers()).asInstanceOf[Future[Option[(Phrase, Phrase)]]])   
        }
        case endWork() => {
        	nbr = nbr - 1
        	if (nbr==0) {
        		println(Console.GREEN + "\nLe poeme est fini !\nMerci de votre attention !" + Console.RESET)
        		Main.system.shutdown
        	}
        }
    }

    def takeOnePoete = Random.nextInt(2) match {
    	case 0 => poete1
    	case _ => poete2
    }

    def traiterPhrases(p:Option[(Phrase, Phrase)]) = p match {
    	case Some(x) => {
    		println(Console.YELLOW + toStr(x) + Console.RESET)
            implicit val timeout = Timeout(50000 millis)
    		Random.nextInt(4) match {
    			case 0 => traiterReception((takeOnePoete ? newDeuxVersPhrase(x._2)).asInstanceOf[Future[Option[(Phrase, Phrase)]]])
    			case 1 => traiterReception((takeOnePoete ? newDeuxVersNbr(Random.nextInt(10)+10)).asInstanceOf[Future[Option[(Phrase, Phrase)]]])
    			case _ => self ! endWork()
    		}
    		
    	}
    	case None => {
    		println(Console.RED + "Le corpus n'est pas assez complet, il n'y a pas de phrases qui riment" + Console.RESET)
    		Main.system.shutdown
    	}
    }

    def traiterReception(message:Future[Option[(Phrase, Phrase)]]):Unit = {
    	message.onComplete {
    		case Success(x) => traiterPhrases(x)
    		case Failure(ex) => {
    			println(Console.RED + "Le fichier (corpus de base) " + ex.getMessage + Console.RESET)
    			Main.system.shutdown
    		}
    	}
    }

    def toStr(t:(Phrase, Phrase)):String = t match {
        case (p1, p2) => p1.toString + "\n" + p2.toString
        case _ => ""
    }
    
}

/*
 * Acteur qui a pour role d'ecrire des poemes
 */
class Poete(chemin_corpus:String, chemin_dictionnaire:String) extends Actor{

    val chemin_corpus_default = "corpus.txt"
    val chemin_dictionnaire_default = "dicorimes.dmp"

    // Chargement des phrases via le corpus en parametre. S'il y a une erreur => utilisation des fichiers par default
    val phrases = Phrases.extraire_phrases(chemin_corpus, chemin_dictionnaire) recoverWith {
        case _ => Phrases.extraire_phrases(chemin_corpus_default, chemin_dictionnaire_default)
       	}

    def receive = {
        case newDeuxVersPhrase(phrase) => generateDeuxVersRimePhrase(phrase) pipeTo sender
        case newDeuxVersNbr(nbr) => generateDeuxVersNbrSyllabes(nbr) pipeTo sender
        case newDeuxVers() => generateDeuxVers pipeTo sender
    }

    def generateDeuxVers = for {
        p <- phrases
    } yield new DeuxVers(p).ecrire

    def generateDeuxVersRimePhrase(x:Phrase) = for {
        p <- phrases
    } yield new DeuxVersRimePhrase(p, x).ecrire

    def generateDeuxVersNbrSyllabes(x:Int) = for {
        p <- phrases
    } yield new DeuxVersNbrSyllabes(p, x).ecrire

    def toStr(t:(Phrase, Phrase)):String = t match {
        case (p1, p2) => p1.toString + "\n" + p2.toString
        case _ => ""
    }
}

abstract class Poeme(phrases:List[Phrase]){
    // Renvoie des phrases aléatoirement
    def choose = for 
    {
        i<-List.range(0,phrases.length)
    } yield phrases((new Random).nextInt.abs % phrases.length)
    
    // Renvoie au hasard des couples de phrases qui riment
    def choose_deux = {
        Random.shuffle(for {
            p1<-phrases
            p2<-phrases if (p1!=p2) && (!p1.toString.equals(p2.toString)) && (p1 rime_avec p2)
        } yield (p1,p2))
    }

    // Renvoie un poeme de deux vers
    def ecrire():Option[(Phrase, Phrase)]

    // Renvoie un poeme de deux vers qui a du sens
    def takeFirstWithMeaning(p:List[(Phrase, Phrase)]):Option[(Phrase, Phrase)] = p match {
        case x :: xs => if (x._1 memeSens x._2) Some(x) else takeFirstWithMeaning(xs) 
        case _ => None
     }
}
class DeuxVers(phrases:List[Phrase]) extends Poeme(phrases:List[Phrase]){
   
    def ecrire = (choose_deux filter { case (x,y) => x.syllabes - y.syllabes >= -2 && x.syllabes - y.syllabes <= 2}) match {
        case x :: xs => takeFirstWithMeaning(x :: xs)
        case _ => None
    }
}

class DeuxVersRimePhrase(phrases:List[Phrase], phrase:Phrase) extends Poeme(phrases:List[Phrase]){
    def ecrire = (choose_deux filter { case (x,y) => x.syllabes - y.syllabes >= -2 && x.syllabes - y.syllabes <= 2 && (x rime_avec phrase)}) match {
        case x :: xs => takeFirstWithMeaning(x :: xs)
        case _ => None
    }
}

class DeuxVersNbrSyllabes(phrases:List[Phrase], nbr:Int) extends Poeme(phrases:List[Phrase]){
    def ecrire = (choose_deux filter { case (x,y) => x.syllabes - y.syllabes >= -2 && x.syllabes - y.syllabes <= 2 && x.syllabes + y.syllabes == nbr}) match {
        case x :: xs => takeFirstWithMeaning(x :: xs)
        case _ => None
    }
}

class Mot(mot:String, nbrSyllabes:Int, phone:String) {

    val syllabes = nbrSyllabes
    val son = phone

    override def toString():String = "(" + mot + ", " + nbrSyllabes + ", " + phone + ")"

    private val voyelles = Set('a','e','i','o','u','y','à','è','ù','é','â','ê','î','ô','û','ä','ë','ï','ö','ü','E','§','2','5','9','8','£','@')

    private def couperMot(mot:String):List[Phone] = (for {
        c<-mot
    } yield {if (voyelles.contains(c)) Voyelle(c) else Consonne(c)}).toList

    // Determine si deux mots riment ensemble
    def rime_avec(autre_mot:Mot):Boolean = rime((couperMot(son), couperMot(autre_mot.son))) 

    // Determine si deux listes de phones riment ensemble
    def rime(m:(List[Phone], List[Phone])):Boolean = m match {
        case (_, Nil) => false
        case (Nil, _) => false
        case (m1, m2) => m1.last match {
            case Voyelle(c) => if (m2.last == Voyelle(c)) true else false
            case Consonne(c) => if (m2.last == Consonne(c)) (rime(m1.reverse.tail.reverse, m2.reverse.tail.reverse)) else false
        }
    }
}

abstract class Message
case class askStrophe() extends Message
case class endWork() extends Message
case class newDeuxVers() extends Message
case class newDeuxVersPhrase(p:Phrase) extends Message
case class newDeuxVersNbr(i:Int) extends Message

abstract class Phone
case class Voyelle(c:Char) extends Phone 
case class Consonne(c:Char) extends Phone

class Phrase(phrase:String,mots_hachage:Map[String,Mot]){

    private val tokens = Phrases.split_mots(phrase.toLowerCase)
      
    val mots = for {
        t<-tokens
    } yield mots_hachage(t)

    override def toString():String = phrase
    
    def memeSens(p:Phrase):Boolean = {
        val sac1 = Future {sacMot}
        val sac2 = Future {p.sacMot}

        val result = for {
            s1 <- sac1
            s2 <- sac2
            sacTotal = s1.intersect(s2)
            
        } yield if ((sacTotal.size.toFloat/(s1.size + s2.size)) >= 0.1) true else false

        val test = result andThen {
            case Success(x) => x
            case Failure(_) => false
        }
        try {Await.result(test, 25000 millis)} catch {
            case _ : java.net.UnknownHostException => true
            case e : Throwable => false
        }      
    }   

    def sacMot = (for {
        t <- tokens
        m <- getWiki(t)
    } yield m).toSet

    val syllabes:Int = (mots.map (x => x.syllabes)).sum
    
    // Determine si deux phrases riment ensemble
    def rime_avec(phrs:Phrase):Boolean = mots.size match {
        case 0 => false
        case _ => phrs.mots.size match {
                case 0 => false
                case _ => mots.last.rime_avec(phrs.mots.last)
            }
    }

    // Recuperer les mots de la reponse de wikipedia pour le mot
    def getWiki(mot:String) = {
        val url = "https://fr.wikipedia.org/w/api.php?action=query&titles=" + mot + "&prop=revisions&rvprop=content&format=xml"
        Phrases.split_mots((XML.load(url) \\ "rev").toString)
    }
}

object Phrase{
    def apply(phrase:String,mots_hachage:Map[String,Mot]) = new Phrase(phrase,mots_hachage)
}

object Phrases{
    def split_mots(s:String):Array[String] =  s.trim.toLowerCase.split("[- ’—,;'()\"!.:?«…/=_{}<>*|\\]\\[\\r\\n]+")
    
    def split_phrases(s:String):Array[String] = s.split("(?<=[.!?:])")
    
    def lire_csv(chemin:String,mots:Set[String]):List[String] ={ (for {
        line <- Source.fromFile(chemin).getLines() 
        line2 =line.toLowerCase if mots contains line2.split(",")(1)
    } yield line2).toList }

    def lire_corpus(chemin:String):Future[String] = Future {
        Source.fromFile(chemin).getLines().filter(_!="").foldLeft(""){_+_}
    }

    def extraire_phrases(chemin_texte:String,chemin_dictionnaire:String):Future[List[Phrase]] = {
        val t = lire_corpus(chemin_texte)
        for {
            texte <- t
            phrases_txt = split_phrases(texte)
            mots_set = (phrases_txt flatMap (x => split_mots(x))).toSet

            dico = lire_csv(chemin_dictionnaire,mots_set)
            mots_hachage = ((dico map (x => (x, x.split(",")))) map {case (x, y) => (y(1), new Mot(y(1), y(6).toInt, y(8)))}).toMap
            phrases = phrases_txt filter (p => (((split_mots(p) map (mots_hachage contains _)) forall (x=>x)) && p.trim!="")) map (p => Phrase(p.trim,mots_hachage))
        } yield phrases.toList
    }
}