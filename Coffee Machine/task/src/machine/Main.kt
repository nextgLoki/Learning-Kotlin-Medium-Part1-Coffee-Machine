package machine

class CoffeeMachine(
    private var money: Int = 550,
    private var water: Int = 400,
    private var milk: Int = 540,
    private var beans: Int = 120,
    private var cups: Int = 9
) {
    fun getState() = """
        The coffee machine has:
        $water of water
        $milk of milk
        $beans of coffee beans
        $cups of disposable cups
        $$money of money
    """.trimIndent()

    enum class MakingCoffeeStates(val itemName: String) {
        NOT_ENOUGH_WATER("water"),
        NOT_ENOUGH_MILK("milk"),
        NOT_ENOUGH_BEANS("beans"),
        NOT_ENOUGH_CUPS("cups"),
        ENOUGH("")
    }

    private fun makeCoffee(requiredWater: Int, requiredMilk: Int, requiredBeans: Int, cost: Int): MakingCoffeeStates {
        when {
            water < requiredWater -> return MakingCoffeeStates.NOT_ENOUGH_WATER
            milk < requiredMilk -> return MakingCoffeeStates.NOT_ENOUGH_MILK
            beans < requiredBeans -> return MakingCoffeeStates.NOT_ENOUGH_BEANS
            cups < 1 -> return MakingCoffeeStates.NOT_ENOUGH_CUPS
        }

        water -= requiredWater
        milk -= requiredMilk
        beans -= requiredBeans
        cups--
        money += cost
        return MakingCoffeeStates.ENOUGH
    }

    fun makeEspresso() = makeCoffee(250, 0, 16, 4)
    fun makeLatte() = makeCoffee(350, 75, 20, 7)
    fun makeCappuccino() = makeCoffee(200, 100, 12, 6)

    fun takeMoney(): Int = money.also { money = 0 }

    fun fill(water: Int, milk: Int, beans: Int, cups: Int) {
        this.water += water
        this.milk += milk
        this.beans += beans
        this.cups += cups
    }
}

class CoffeeMachineManager(private val cm: CoffeeMachine) {
    enum class UserActions {
        BUY, TAKE, FILL, REMAINING, EXIT;

        companion object {
            override fun toString() = values().joinToString(", ") { it.name.lowercase() }
        }
    }

    private fun inputIntWithMessage(message: String) = print(message)
        .let { readLine()!!.toInt() }

    private fun inputStrWithMessage(message: String) = print(message)
        .let { readLine()!! }

    private fun inputAction() = inputStrWithMessage("Write action ($UserActions): ")

    private fun processFill(cm: CoffeeMachine) = cm.fill(
        inputIntWithMessage("Write how many ml of water do you want to add: "),
        inputIntWithMessage("Write how many ml of milk do you want to add: "),
        inputIntWithMessage("Write how many grams of coffee beans do you want to add: "),
        inputIntWithMessage("Write how many disposable cups of coffee do you want to add: "),
    )

    private fun processRequestBuy(cm: CoffeeMachine) {
        val msg = "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: "
        when (inputStrWithMessage(msg)) {
            "1" -> printCoffeeBuyState(cm.makeEspresso())
            "2" -> printCoffeeBuyState(cm.makeLatte())
            "3" -> printCoffeeBuyState(cm.makeCappuccino())
            "back" -> return
        }
    }

    private fun printCoffeeBuyState(state: CoffeeMachine.MakingCoffeeStates) = when (state) {
        CoffeeMachine.MakingCoffeeStates.ENOUGH -> println("I have enough resources, making you a coffee!")
        else -> println("Sorry, not enough ${state.itemName}!")
    }

    fun run() {
        while (true) {
            when (UserActions.valueOf(inputAction().uppercase())) {
                UserActions.BUY -> processRequestBuy(cm)
                UserActions.TAKE -> println("I gave you $${cm.takeMoney()}")
                UserActions.FILL -> processFill(cm)
                UserActions.REMAINING -> println(cm.getState())
                UserActions.EXIT -> return
            }
        }
    }
}

fun main() = CoffeeMachineManager(CoffeeMachine()).run()
