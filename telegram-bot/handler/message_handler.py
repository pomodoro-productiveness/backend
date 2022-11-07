import os

from aiogram import Bot, Dispatcher, types

from rest.client import rest_client

token = os.environ.get('TELEGRAM_BOT_TOKEN')
bot = Bot(token=token)
dp = Dispatcher(bot)


@dp.message_handler(commands='save')
async def say_hi(message: types.Message):
    slots = rest_client.get_slots()
    await bot.send_message(message.from_user.id, slots)
