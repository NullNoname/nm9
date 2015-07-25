// Sample Tetromino game
function init() {
	java.lang.System.out.println("Script loaded");
}

function shutdown() {
	java.lang.System.out.println("Script shutdown");
}

function gsReadyGameStart(gamePlay) {
	java.lang.System.out.println("Game start");
}

function publishEvent(gameManager, event) {
	//java.lang.System.out.println("output event:" + event.class.getCanonicalName());
}

function processEvent(gameManager, event) {
	//java.lang.System.out.println("input event:" + event.class.getCanonicalName());
}
