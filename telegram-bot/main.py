from aiogram.utils import executor
from message_handler import dp

# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    executor.start_polling(dp, skip_updates=True)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
