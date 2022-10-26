from aiogram import Bot, Dispatcher, executor, types

bot = Bot(token='5735498531:AAHaOtfDKaxwarQmlCoeJzoo89OKOdvuUKg')
dp = Dispatcher(bot)


@dp.message_handler(commands='start')
async def say_hi(message: types.Message):
    await bot.send_message(message.from_user.id, 'Hi')
